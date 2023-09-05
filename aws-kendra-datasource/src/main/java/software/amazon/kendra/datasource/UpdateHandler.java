package software.amazon.kendra.datasource;

import com.google.common.collect.Sets;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static software.amazon.kendra.datasource.ApiName.UPDATE_DATASOURCE;

public class UpdateHandler extends BaseHandlerStd {

    private Logger logger;

    private final DataSourceArnBuilder dataSourceArnBuilder;

    public UpdateHandler() {
       super();
       dataSourceArnBuilder = new DataSourceArn();
    }

    public UpdateHandler(DataSourceArnBuilder dataSourceArnBuilder) {
       super();
       this.dataSourceArnBuilder = dataSourceArnBuilder;
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
                .then(progress ->
                        proxy.initiate("AWS-Kendra-DataSource::ValidateResourceExists", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall(this::validateResourceExists)
                                .progress()
                )
                .then(progress ->
                        proxy.initiate("AWS-Kendra-DataSource::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .makeServiceCall(this::updateDataSource)
                                .stabilize(this::stabilize)
                                .progress())
                .then(progress -> updateTags(proxyClient, progress, request))
                .then(progress -> new ReadHandler(dataSourceArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private DescribeDataSourceResponse validateResourceExists(DescribeDataSourceRequest describeDataSourceRequest, ProxyClient<KendraClient> proxyClient) {
        DescribeDataSourceResponse describeDataSourceResponse;
        try {
            describeDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(describeDataSourceRequest, proxyClient.client()::describeDataSource);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeDataSourceRequest.id(), e);
        }
        return describeDataSourceResponse;
    }

    /**
    * Implement client invocation of the update request through the proxyClient, which is already initialised with
    * caller credentials, correct region and retry settings
    * @param updateDataSourceRequest the aws service request to update a resource
    * @param proxyClient the aws service client to make the call
    * @return update resource response
    */
    private UpdateDataSourceResponse updateDataSource(
        final UpdateDataSourceRequest updateDataSourceRequest,
        final ProxyClient<KendraClient> proxyClient) {
            UpdateDataSourceResponse updateDataSourceResponse;
        try {
            updateDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(updateDataSourceRequest, proxyClient.client()::updateDataSource);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, updateDataSourceRequest.id(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (ServiceQuotaExceededException e) {
            throw new CfnServiceLimitExceededException(ResourceModel.TYPE_NAME, e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(UPDATE_DATASOURCE, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(UPDATE_DATASOURCE, e);
        } catch (final AwsServiceException e) {
           /*
            * While the handler contract states that the handler must always return a progress event,
            * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
            * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
            * to more specific error codes
            */
            throw new CfnGeneralServiceException(UPDATE_DATASOURCE, e);
        }

        logger.log(String.format("%s has successfully been updated.", ResourceModel.TYPE_NAME));
        return updateDataSourceResponse;
    }

   /**
    * If your resource requires some form of stabilization (e.g. service does not provide strong consistency), you will need to ensure that your code
    * accounts for any potential issues, so that a subsequent read/update requests will not cause any conflicts (e.g. NotFoundException/InvalidRequestException)
    * for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
    * @param updateDataSourceRequest the aws service  update resource resquest
    * @param updateDataSourceResponse the aws service  update resource response
    * @param proxyClient the aws service client to make the call
    * @param model resource model
    * @param callbackContext callback context
    * @return boolean state of stabilized or not
    */
    private boolean stabilize(
        final UpdateDataSourceRequest updateDataSourceRequest,
        final UpdateDataSourceResponse updateDataSourceResponse,
        final ProxyClient<KendraClient> proxyClient,
        final ResourceModel model,
        final CallbackContext callbackContext) {
        DescribeDataSourceRequest describeDataSourceRequest = DescribeDataSourceRequest.builder()
            .id(model.getId())
            .indexId(model.getIndexId())
            .build();
        DescribeDataSourceResponse describeDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(describeDataSourceRequest,
            proxyClient.client()::describeDataSource);
        DataSourceStatus dataSourceStatus = describeDataSourceResponse.status();
        return dataSourceStatus.equals(DataSourceStatus.ACTIVE);
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(final ProxyClient<KendraClient> proxyClient,
        final ProgressEvent<ResourceModel, CallbackContext> progress, ResourceHandlerRequest<ResourceModel> request) {
        ResourceModel currResourceModel = request.getDesiredResourceState();
        ResourceModel prevResourceModel = request.getPreviousResourceState();
        CallbackContext callbackContext = progress.getCallbackContext();
        Set<Tag> currentTags;
        if (currResourceModel.getTags() != null) {
            currentTags = currResourceModel.getTags().stream().collect(Collectors.toSet());
        } else {
            currentTags = new HashSet<>();
        }

        String arn = dataSourceArnBuilder.build(request);
        Set<Tag> existingTags = new HashSet<>();
        if (prevResourceModel != null && prevResourceModel.getTags() != null) {
            existingTags = prevResourceModel.getTags().stream().collect(Collectors.toSet());
        }

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
        return ProgressEvent.progress(currResourceModel, callbackContext);
    }

   /**
    * Checks the if the create only fields have been updated and throws an exception if it is the case
    * @param currModel the current resource model
    * @param prevModel the previous resource model
    */
    private void verifyNonUpdatableFields(ResourceModel currModel, ResourceModel prevModel) {
        if (prevModel != null) {
            if (!Optional.ofNullable(currModel.getType()).equals(Optional.ofNullable(prevModel.getType()))) {
                throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "Type");
            }
        }
    }
}
