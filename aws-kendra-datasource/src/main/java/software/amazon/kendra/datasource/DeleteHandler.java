package software.amazon.kendra.datasource;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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

import static software.amazon.kendra.datasource.ApiName.DELETE_DATASOURCE;

public class DeleteHandler extends BaseHandlerStd {

    private static final Constant STABILIZATION_DELAY = Constant.of()
            // Set the timeout to something silly/way too high, because
            // we already set the timeout in the schema https://github.com/aws-cloudformation/aws-cloudformation-resource-schema
            .timeout(Duration.ofDays(365L))
            // Set the delay to five minutes so the stabilization code only calls
            // DescribeDataSource every five minutes - delete can take hours
            .delay(Duration.ofMinutes(5))
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

        return ProgressEvent.progress(model, callbackContext)
                // STEP 2.0 [delete/stabilize progress chain - required for resource deletion]
                .then(progress ->
                        // If your service API throws 'ResourceNotFoundException' for delete requests then DeleteHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        proxy.initiate("AWS-Kendra-DataSource::Delete", proxyClient, model, callbackContext)

                                // STEP 2.1 [construct a body of a request]
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .backoffDelay(delay)
                                // STEP 2.2 [ make an api call]
                                .makeServiceCall(this::deleteDataSource)

                                // STEP 2.3 [stabilize step is not necessarily required but typically involves describing the resource until it is in a certain status, though it can take many forms]
                                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                                .stabilize(this::stabilizedOnDelete)
                                .done(this::setResourceModelToNullAndReturnSuccess));
    }

    private ProgressEvent<ResourceModel, CallbackContext> setResourceModelToNullAndReturnSuccess(
            DeleteDataSourceRequest deleteDataSourceRequest,
            DeleteDataSourceResponse deleteDataSourceResponse,
            ProxyClient<KendraClient> proxyClient,
            ResourceModel resourceModel,
            CallbackContext callbackContext) {
        return ProgressEvent.defaultSuccessHandler(null);
    }


    /**
     * Implement client invocation of the delete request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param deleteDataSourceRequest the aws service request to delete a resource
     * @param proxyClient the aws service client to make the call
     * @return delete resource response
     */
    private DeleteDataSourceResponse deleteDataSource(
            final DeleteDataSourceRequest deleteDataSourceRequest,
            final ProxyClient<KendraClient> proxyClient) {
        DeleteDataSourceResponse deleteDataSourceResponse;
        try {
            deleteDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(
                    deleteDataSourceRequest, proxyClient.client()::deleteDataSource);
        } catch (final ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, deleteDataSourceRequest.id(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(DELETE_DATASOURCE, e);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(DELETE_DATASOURCE, e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(DELETE_DATASOURCE, e);
        }

        logger.log(String.format("%s successfully deleted.", ResourceModel.TYPE_NAME));
        return deleteDataSourceResponse;
    }

    /**
     * If deletion of your resource requires some form of stabilization (e.g. propagation delay)
     * for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
     * @param deleteDataSourceRequest the aws service request to delete a resource
     * @param deleteDataSourceResponse the aws service response to delete a resource
     * @param proxyClient the aws service client to make the call
     * @param model resource model
     * @param callbackContext callback context
     * @return boolean state of stabilized or not
     */
    private boolean stabilizedOnDelete(
            final DeleteDataSourceRequest deleteDataSourceRequest,
            final DeleteDataSourceResponse deleteDataSourceResponse,
            final ProxyClient<KendraClient> proxyClient,
            final ResourceModel model,
            final CallbackContext callbackContext) {

        DescribeDataSourceRequest describeDataSourceRequest = DescribeDataSourceRequest.builder()
                .id(model.getId())
                .indexId(model.getIndexId())
                .build();

        boolean stabilized;
        try {
            proxyClient.injectCredentialsAndInvokeV2(describeDataSourceRequest, proxyClient.client()::describeDataSource);
            stabilized = false;
        } catch (ResourceNotFoundException e) {
            stabilized = true;
        }
        logger.log(String.format("%s [%s] deletion has stabilized: %s",
                ResourceModel.TYPE_NAME, model.getPrimaryIdentifier(), stabilized));
        return stabilized;
    }
}
