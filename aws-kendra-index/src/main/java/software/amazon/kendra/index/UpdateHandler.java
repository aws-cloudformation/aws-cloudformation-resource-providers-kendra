package software.amazon.kendra.index;

import com.google.common.collect.Sets;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Delay;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandlerStd {

    private static final Constant STABILIZATION_DELAY = Constant.of()
            // Set the timeout to something silly/way too high, because
            // we already set the timeout in the schema https://github.com/aws-cloudformation/aws-cloudformation-resource-schema
            .timeout(Duration.ofDays(365L))
            // Set the delay to two minutes so the stabilization code only calls
            // DescribeIndex every two minutes - update takes
            // 30/45+ minutes so there's no need to check the index is active more than every couple minutes.
            .delay(Duration.ofMinutes(2))
            .build();

    private Delay delay;

    private Logger logger;

    private IndexArnBuilder indexArnBuilder;

    public UpdateHandler() {
        super();
        indexArnBuilder = new IndexArn();
        delay = STABILIZATION_DELAY;
    }

    public UpdateHandler(IndexArnBuilder indexArnBuilder, Delay delay) {
        super();
        this.indexArnBuilder = indexArnBuilder;
        this.delay = delay;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<KendraClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();

        verifyNonUpdatableFields(model, request.getPreviousResourceState());

        return ProgressEvent.progress(model, callbackContext)
                // First validate the resource actually exists per the contract requirements
                // https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                .then(progress ->
                        proxy.initiate("AWS-Kendra-Index::ValidateResourceExists", proxyClient, model, callbackContext)
                                .translateToServiceRequest(resourceModel -> Translator.translateToReadRequest(model))
                                .makeServiceCall(this::validateResourceExists)
                                .progress())
                .then(progress ->
                        proxy.initiate("AWS-Kendra-Index::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(resourceModel -> translateToUpdateRequest(model, request.getPreviousResourceState()))
                                .backoffDelay(delay)
                                .makeServiceCall(this::updateIndex)
                                .stabilize(this::stabilize)
                                .progress())
                .then(progress -> updateTags(proxyClient, progress, request))
                .then(progress -> new ReadHandler(indexArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private DescribeIndexResponse validateResourceExists(DescribeIndexRequest describeIndexRequest, ProxyClient<KendraClient> proxyClient) {
        DescribeIndexResponse describeIndexResponse;
        try {
            describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,proxyClient.client()::describeIndex);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeIndexRequest.id(), e);
        }
        return describeIndexResponse;
    }

    static UpdateIndexRequest translateToUpdateRequest(final ResourceModel model,
                                                       final ResourceModel prevModel) {
        try {
            return Translator.translateToUpdateRequest(model, prevModel);
        } catch (TranslatorValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        }
    }

    private boolean stabilize(
            final UpdateIndexRequest updateIndexRequest,
            final UpdateIndexResponse updateIndexResponse,
            final ProxyClient<KendraClient> proxyClient,
            final ResourceModel model,
            final CallbackContext callbackContext) {
        DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
                .id(model.getId())
                .build();
        DescribeIndexResponse describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
                proxyClient.client()::describeIndex);
        IndexStatus indexStatus = describeIndexResponse.status();
        return indexStatus.equals(IndexStatus.ACTIVE);
    }

    /**
     * Implement client invocation of the update request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param updateIndexRequest the aws service request to update a resource
     * @param proxyClient the aws service client to make the call
     * @return update resource response
     */
    private UpdateIndexResponse updateIndex(
            final UpdateIndexRequest updateIndexRequest,
            final ProxyClient<KendraClient> proxyClient) {
        UpdateIndexResponse updateIndexResponse;
        // In this code block we assume the previous DescribeIndex API call validated the resource exists and so doesn't
        // catch and re-throw here.
        try {
            updateIndexResponse = proxyClient.injectCredentialsAndInvokeV2(updateIndexRequest, proxyClient.client()::updateIndex);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (ServiceQuotaExceededException e) {
            throw new CfnServiceLimitExceededException(ResourceModel.TYPE_NAME, e.getMessage(), e.getCause());
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME + e.getMessage(), e);
        }

        logger.log(String.format("%s has successfully been updated.", ResourceModel.TYPE_NAME));
        return updateIndexResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
            final ProxyClient<KendraClient> proxyClient,
            final ProgressEvent<ResourceModel, CallbackContext> progress,
            ResourceHandlerRequest<ResourceModel> request) {
        ResourceModel resourceModel = progress.getResourceModel();
        CallbackContext callbackContext = progress.getCallbackContext();
        Set<Tag> currentTags;
        if (resourceModel.getTags() != null) {
            currentTags = resourceModel.getTags().stream().collect(Collectors.toSet());
        } else {
            currentTags = new HashSet<>();
        }

        String arn = indexArnBuilder.build(request);
        ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsRequest(arn);
        List<software.amazon.awssdk.services.kendra.model.Tag> existingTagsSdk = proxyClient.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest, proxyClient.client()::listTagsForResource).tags();
        Set<Tag> existingTags = existingTagsSdk.stream().map(x -> Tag.builder().key(x.key()).value(x.value()).build())
                .collect(Collectors.toSet());

        final Set<Tag> tagsToAdd = Sets.difference(currentTags, existingTags);
        if (!tagsToAdd.isEmpty()) {
            TagResourceRequest tagResourceRequest = Translator.translateToTagResourceRequest(tagsToAdd, arn);
            try {
                proxyClient.injectCredentialsAndInvokeV2(tagResourceRequest, proxyClient.client()::tagResource);
            } catch (ValidationException e) {
                throw new CfnInvalidRequestException(e.getMessage(), e);
            }
        }

        final Set<Tag> tagsToRemove = Sets.difference(existingTags, currentTags);
        if (!tagsToRemove.isEmpty()) {
            UntagResourceRequest untagResourceRequest = Translator.translateToUntagResourceRequest(tagsToRemove, arn);
            try {
                proxyClient.injectCredentialsAndInvokeV2(untagResourceRequest, proxyClient.client()::untagResource);
            } catch (ValidationException e) {
                throw new CfnInvalidRequestException(e.getMessage(), e);
            }
        }
        return ProgressEvent.progress(resourceModel, callbackContext);
    }

   /**
    * Checks the if the create only fields have been updated and throws an exception if it is the case
    * @param currModel the current resource model
    * @param prevModel the previous resource model
    */
    private void verifyNonUpdatableFields(ResourceModel currModel, ResourceModel prevModel) {
        if (prevModel != null) {
            if (!Optional.ofNullable(currModel.getEdition()).equals(Optional.ofNullable(prevModel.getEdition()))) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "Edition");
            }
            if (!Optional.ofNullable(currModel.getServerSideEncryptionConfiguration()).equals(
              Optional.ofNullable(prevModel.getServerSideEncryptionConfiguration()))) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "ServerSideEncryptionConfiguration");
            }
        }
    }
}
