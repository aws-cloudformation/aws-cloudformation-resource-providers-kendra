package software.amazon.kendra.datasource;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

   private Logger logger;

   private DataSourceArnBuilder dataSourceArnBuilder;

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

       return ProgressEvent.progress(model, callbackContext)
           // STEP 1 [first update/stabilize progress chain - required for resource update]
           .then(progress ->
               // STEP 1.0 [initialize a proxy context]
               proxy.initiate("AWS-Kendra-DataSource::Update", proxyClient, model, callbackContext)

                   // STEP 1.1 [construct a body of a request]
                   .translateToServiceRequest(Translator::translateToUpdateRequest)

                   // STEP 1.2 [ make an api call]
                   .makeServiceCall(this::updateDataSource)

                   // STEP 1.3 [stabilize step is not necessarily required but typically involves describing the resource until it is in a certain status, though it can take many forms]
                   // stabilization step may or may not be needed after each API call
                   // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                   .stabilize(this::stabilize)
                   .progress())

           // STEP 3 [TODO: describe call/chain to return the resource model]
           .then(progress -> new ReadHandler(dataSourceArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
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
           throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME + e.getMessage(), e);
       } catch (ResourceNotFoundException e) {
           throw new CfnNotFoundException(ResourceModel.TYPE_NAME, updateDataSourceRequest.id(), e);
       } catch (ConflictException e) {
           throw new CfnResourceConflictException(e);
       } catch (final AwsServiceException e) {
           /*
            * While the handler contract states that the handler must always return a progress event,
            * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
            * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
            * to more specific error codes
            */
           throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
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
}
