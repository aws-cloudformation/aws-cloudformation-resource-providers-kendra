package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.CreateIndexResponse;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Delay;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;

import static software.amazon.kendra.index.ApiName.CREATE_INDEX;
import static software.amazon.kendra.index.ApiName.UPDATE_INDEX;

public class CreateHandler extends BaseHandlerStd {

    private static final Constant STABILIZATION_DELAY = Constant.of()
            // Set the timeout to something silly/way too high, because
            // we already set the timeout in the schema https://github.com/aws-cloudformation/aws-cloudformation-resource-schema
            .timeout(Duration.ofDays(365L))
            // Set the delay to two minutes so the stabilization code only calls
            // DescribeIndex every two minutes - create takes
            // 30/45+ minutes so there's no need to check the index is active more than every couple minutes.
            .delay(Duration.ofMinutes(2))
            .build();

    private final Delay delay;

    private static final BiFunction<ResourceModel, ProxyClient<KendraClient>, ResourceModel> EMPTY_CALL =
            (model, proxyClient) -> model;

    private Logger logger;

    private final IndexArnBuilder indexArnBuilder;

    public CreateHandler() {
        super();
        indexArnBuilder = new IndexArn();
        delay = STABILIZATION_DELAY;
    }

    // Used for testing.
    public CreateHandler(IndexArnBuilder indexArnBuilder, Delay delay) {
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

        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)

                // STEP 1 [check if resource already exists]
                // if target API does not support 'ResourceAlreadyExistsException' then following check is required
                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                //.then(progress -> checkForPreCreateResourceExistence(proxy, request, progress))
                // STEP 2 [create progress chain - required for resource creation]
                .then(progress ->
                        // If your service API throws 'ResourceAlreadyExistsException' for create requests then CreateHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        proxy.initiate("AWS-Kendra-Index::Create", proxyClient, request.getDesiredResourceState(), callbackContext)
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall(this::createIndex)
                                .done(this::setId)
                )
                // stabilize
                .then(progress -> stabilize(proxy, proxyClient, progress, "AWS-Kendra-Index::PostCreateStabilize"))
                .then(progress ->
                        // If your resource is provisioned through multiple API calls, you will need to apply each subsequent update
                        // STEP 3.0 [initialize a proxy context]
                        proxy.initiate("AWS-Kendra-Index::PostCreateUpdate", proxyClient, request.getDesiredResourceState(), callbackContext)
                                .translateToServiceRequest(this::translateToPostCreateUpdateIndexRequest)
                                .makeServiceCall(this::postCreate)
                                .progress()
                )
                // stabilize again because VCU changes can cause the index to enter UPDATING state
                .then(progress -> stabilize(proxy, proxyClient, progress, "AWS-Kendra-Index::PostCreateUpdateStabilize"))
                .then(progress -> new ReadHandler(indexArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateIndexRequest translateToPostCreateUpdateIndexRequest(final ResourceModel resourceModel) {
        try {
            return Translator.translateToPostCreateUpdateRequest(resourceModel);
        } catch (TranslatorValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> setId(CreateIndexRequest createIndexRequest,
                                                                CreateIndexResponse createIndexResponse,
                                                                ProxyClient<KendraClient> proxyClient,
                                                                ResourceModel resourceModel,
                                                                CallbackContext callbackContext) {

        resourceModel.setId(createIndexResponse.id());
        return ProgressEvent.progress(resourceModel, callbackContext);
    }


    /**
     * Implement client invocation of the create request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param createIndexRequest the aws service request to create a resource
     * @param proxyClient the aws service client to make the call
     * @return createIndexResponse create resource response
     */
    private CreateIndexResponse createIndex(
            final CreateIndexRequest createIndexRequest,
            final ProxyClient<KendraClient> proxyClient) {
        CreateIndexResponse createIndexResponse;
        try {
            createIndexResponse = proxyClient.injectCredentialsAndInvokeV2(createIndexRequest, proxyClient.client()::createIndex);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (ServiceQuotaExceededException e) {
            throw new CfnServiceLimitExceededException(ResourceModel.TYPE_NAME, e.getMessage(), e.getCause());
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(CREATE_INDEX, e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(CREATE_INDEX, e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(CREATE_INDEX, e);
        }

        logger.log(String.format("%s successfully called CreateIndex and received index ID %s. Still need to stabilize.", ResourceModel.TYPE_NAME, createIndexResponse.id()));
        return createIndexResponse;
    }

    /**
     * If your resource is provisioned through multiple API calls, you will need to apply each subsequent update
     * step in a discrete call/stabilize chain to ensure the entire resource is provisioned as intended.
     * @param updateIndexRequest the aws service request to create a resource
     * @param proxyClient the aws service client to make the call
     * @return updateIndexResponse create resource response
     */
    private UpdateIndexResponse postCreate(
            final UpdateIndexRequest updateIndexRequest,
            final ProxyClient<KendraClient> proxyClient) {
        UpdateIndexResponse updateIndexResponse;

        // Map Kendra Errors: https://docs.aws.amazon.com/kendra/latest/APIReference/API_UpdateIndex.html#API_UpdateIndex_Errors
        // to cfn errors.
        try {
            updateIndexResponse = proxyClient.injectCredentialsAndInvokeV2(updateIndexRequest, proxyClient.client()::updateIndex);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (ServiceQuotaExceededException e) {
            throw new CfnServiceLimitExceededException(ResourceModel.TYPE_NAME, e.getMessage(), e.getCause());
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(UPDATE_INDEX, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(UPDATE_INDEX, e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(UPDATE_INDEX, e);
        }

        logger.log(String.format("%s successfully called UpdateIndex with index ID %s. Still need to stabilize.", ResourceModel.TYPE_NAME, updateIndexRequest.id()));
        return updateIndexResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> stabilize(
            final AmazonWebServicesClientProxy proxy,
            final ProxyClient<KendraClient> proxyClient,
            final ProgressEvent<ResourceModel, CallbackContext> progress,
            String callGraph) {
        return proxy.initiate(callGraph, proxyClient, progress.getResourceModel(),
                progress.getCallbackContext())
                .translateToServiceRequest(Function.identity())
                .backoffDelay(delay)
                .makeServiceCall(EMPTY_CALL)
                .stabilize((request, response, proxyInvocation, model, callbackContext) ->
                        isStabilized(proxyInvocation, model)).progress();
    }

    private boolean isStabilized(final ProxyClient<KendraClient> proxyClient, final ResourceModel model) {
        DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
                .id(model.getId())
                .build();
        DescribeIndexResponse describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
                proxyClient.client()::describeIndex);
        IndexStatus indexStatus = describeIndexResponse.status();
        if (indexStatus.equals(IndexStatus.FAILED)) {
            throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, model.getId());
        }
        boolean stabilized = indexStatus.equals(IndexStatus.ACTIVE);
        logger.log(String.format("%s [%s] create has stabilized: %s", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier(), stabilized));
        return stabilized;
    }
}
