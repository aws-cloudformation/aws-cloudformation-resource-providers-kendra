package software.amazon.kendra.datasource;

import java.util.function.BiFunction;
import java.util.function.Function;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.kendra.datasource.ApiName.CREATE_DATASOURCE;

public class CreateHandler extends BaseHandlerStd {

    protected static final BiFunction<ResourceModel, ProxyClient<KendraClient>, ResourceModel> EMPTY_CALL =
            (model, proxyClient) -> model;

    private Logger logger;

    private DataSourceArnBuilder dataSourceArnBuilder;

    public CreateHandler() {
        super();
        this.dataSourceArnBuilder = new DataSourceArn();
    }

    public CreateHandler(DataSourceArnBuilder dataSourceArnBuilder) {
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

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        return ProgressEvent.progress(model, callbackContext)

            // STEP 1 [check if resource already exists]
            // if target API does not support 'ResourceAlreadyExistsException' then following check is required
            // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
            //.then(progress -> checkForPreCreateResourceExistence(proxy, request, progress))

            // STEP 2 [create/stabilize progress chain - required for resource creation]
            .then(progress ->
                // If your service API throws 'ResourceAlreadyExistsException' for create requests then CreateHandler can return just proxy.initiate construction
                // STEP 2.0 [initialize a proxy context]
                proxy.initiate("AWS-Kendra-DataSource::Create", proxyClient, model, callbackContext)
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::createDataSource)
                    .done(this::setId)
                )
            // stabilize
            .then(progress -> stabilize(proxy, proxyClient, progress))
            .then(progress -> new ReadHandler(dataSourceArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }


    /**
     * Implement client invocation of the create request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param createDataSourceRequest the aws service request to create a resource
     * @param proxyClient the aws service client to make the call
     * @return createDataSourceResponse create resource response
     */
    private CreateDataSourceResponse createDataSource(
        final CreateDataSourceRequest createDataSourceRequest,
        final ProxyClient<KendraClient> proxyClient) {
        CreateDataSourceResponse createDataSourceResponse;
        try {
            createDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(createDataSourceRequest,
             proxyClient.client()::createDataSource);
        } catch(final ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (final ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(CREATE_DATASOURCE, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return createDataSourceResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> stabilize(
            final AmazonWebServicesClientProxy proxy,
            final ProxyClient<KendraClient> proxyClient,
            final ProgressEvent<ResourceModel, CallbackContext> progress) {
        return proxy.initiate("AWS-Kendra-DataSource::stabilize", proxyClient, progress.getResourceModel(),
                progress.getCallbackContext())
                .translateToServiceRequest(Function.identity())
                .makeServiceCall(EMPTY_CALL)
                .stabilize((resourceModel, response, proxyInvocation, model, callbackContext) ->
                        isStabilized(proxyInvocation, model)).progress();
    }

    private boolean isStabilized(final ProxyClient<KendraClient> proxyClient, final ResourceModel model) {
        DescribeDataSourceRequest describeDataSourceRequest = DescribeDataSourceRequest.builder()
                .id(model.getId())
                .indexId(model.getIndexId())
                .build();
        DescribeDataSourceResponse describeDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(describeDataSourceRequest,
                proxyClient.client()::describeDataSource);
        DataSourceStatus dataSourceStatus = describeDataSourceResponse.status();
        if (dataSourceStatus.equals(DataSourceStatus.FAILED)) {
            throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, model.getId());
        }
        return dataSourceStatus.equals(DataSourceStatus.ACTIVE);
    }

    private ProgressEvent<ResourceModel, CallbackContext> setId(CreateDataSourceRequest createDataRequest,
        CreateDataSourceResponse createDataSourceResponse, ProxyClient<KendraClient> proxyClient, ResourceModel resourceModel,
        CallbackContext callbackContext) {
        resourceModel.setId(createDataSourceResponse.id());
        return ProgressEvent.progress(resourceModel, callbackContext);
    }
}
