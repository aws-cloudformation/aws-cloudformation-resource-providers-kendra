package software.amazon.kendra.index;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexResponse;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Delay;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;

import static software.amazon.kendra.index.ApiName.DELETE_INDEX;

public class DeleteHandler extends BaseHandlerStd {

    private static final Constant STABILIZATION_DELAY = Constant.of()
            // Set the timeout to something silly/way too high, because
            // we already set the timeout in the schema https://github.com/aws-cloudformation/aws-cloudformation-resource-schema
            .timeout(Duration.ofDays(365L))
            // Set the delay to one minute so the stabilization code only calls
            // DescribeIndex every minute - delete can take a few minutes
            // so there's no need to check the index has been deleted more than once every two minutes.
            .delay(Duration.ofMinutes(2))
            .build();

    private Logger logger;

    private final Delay delay;

    public DeleteHandler() {
        super();
        delay = STABILIZATION_DELAY;
    }

    public DeleteHandler(Delay delay) {
        super();
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

        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        return ProgressEvent.progress(model, callbackContext)

                // STEP 1 [check if resource already exists]
                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                // if target API does not support 'ResourceNotFoundException' then following check is required
                //.then(progress -> checkForPreDeleteResourceExistence(proxy, proxyClient, request, progress))
                .then(progress -> preExistenceCheckForDelete(proxy, proxyClient, progress, request))
                // STEP 2.0 [delete/stabilize progress chain - required for resource deletion]
                .then(progress ->
                        // If your service API throws 'ResourceNotFoundException' for delete requests then DeleteHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        proxy.initiate("AWS-Kendra-Index::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .backoffDelay(delay)
                                .makeServiceCall((awsRequest, sdkProxyClient) -> deleteIndex(awsRequest, sdkProxyClient, callbackContext))
                                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                                .stabilize(this::stabilizedOnDelete)
                                .done(this::setResourceModelToNullAndReturnSuccess));
    }

    private ProgressEvent<ResourceModel, CallbackContext> preExistenceCheckForDelete(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<KendraClient> proxyClient,
        final ProgressEvent<ResourceModel, CallbackContext> progressEvent,
        final ResourceHandlerRequest<ResourceModel> request
    ) {
        ResourceModel model = progressEvent.getResourceModel();
        CallbackContext callbackContext = progressEvent.getCallbackContext();

        logger.log(String.format("%s [%s] pre-existence check for deletion", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));

        DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
                .id(model.getId())
                .build();
        try {
            proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
                    proxyClient.client()::describeIndex);
            return ProgressEvent.progress(model, callbackContext);
        } catch (ResourceNotFoundException e) {
            if (callbackContext.isDeleteWorkflow()) {
                logger.log(String.format("In a delete workflow. Allow ResourceNotFoundException to propagate."));
                return ProgressEvent.progress(model, callbackContext);
            }
            logger.log(String.format("%s [%s] does not pre-exist", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeIndexRequest.id(), e);
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> setResourceModelToNullAndReturnSuccess(
            DeleteIndexRequest deleteIndexRequest,
            DeleteIndexResponse deleteIndexResponse,
            ProxyClient<KendraClient> proxyClient,
            ResourceModel resourceModel,
            CallbackContext callbackContext) {
        return ProgressEvent.defaultSuccessHandler(null);
    }

    /**
     * Implement client invocation of the delete request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param deleteIndexRequest the aws service request to delete a resource
     * @param proxyClient the aws service client to make the call
     * @return delete resource response
     */
    private DeleteIndexResponse deleteIndex(
            final DeleteIndexRequest deleteIndexRequest,
            final ProxyClient<KendraClient> proxyClient,
            final CallbackContext callbackContext) {
        DeleteIndexResponse deleteIndexResponse;
        try {
            deleteIndexResponse = proxyClient.injectCredentialsAndInvokeV2(deleteIndexRequest, proxyClient.client()::deleteIndex);
            callbackContext.setDeleteWorkflow(true);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, deleteIndexRequest.id(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(DELETE_INDEX, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(DELETE_INDEX, e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(DELETE_INDEX, e);
        }

        logger.log(String.format("%s successfully called DeleteIndex with index ID %s. Still need to stabilize.", ResourceModel.TYPE_NAME, deleteIndexRequest.id()));
        return deleteIndexResponse;
    }

    /**
     * If deletion of your resource requires some form of stabilization (e.g. propagation delay)
     * for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
     * @param deleteIndexRequest the aws service request to delete a resource
     * @param deleteIndexResponse the aws service response to delete a resource
     * @param proxyClient the aws service client to make the call
     * @param model resource model
     * @param callbackContext callback context
     * @return boolean state of stabilized or not
     */
    private boolean stabilizedOnDelete(
            final DeleteIndexRequest deleteIndexRequest,
            final DeleteIndexResponse deleteIndexResponse,
            final ProxyClient<KendraClient> proxyClient,
            final ResourceModel model,
            final CallbackContext callbackContext) {

        DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
                .id(model.getId())
                .build();
        boolean stabilized;
        try {
            proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
                    proxyClient.client()::describeIndex);
            stabilized = false;
        } catch (ResourceNotFoundException e) {
            stabilized = true;
        }
        logger.log(String.format("%s [%s] deletion has stabilized: %s", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier(), stabilized));
        return stabilized;
    }
}
