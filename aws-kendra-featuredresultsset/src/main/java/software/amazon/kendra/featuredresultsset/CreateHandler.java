package software.amazon.kendra.featuredresultsset;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsConflictException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.kendra.featuredresultsset.ApiName.CREATE_FEATURED_RESULTS_SET;

public class CreateHandler extends BaseHandlerStd {
    private Logger logger;
    private FeaturedResultsArnBuilder frsBuilder;

    public CreateHandler() {
      super();
      frsBuilder = new FeaturedResultsArn();
    }

    // Used for testing.
    public CreateHandler(FeaturedResultsArnBuilder frsBuilder) {
      super();
      this.frsBuilder = frsBuilder;
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

            // STEP 1 [check if resource already exists]
            // if target API does not support 'ResourceAlreadyExistsException' then following check is required
            // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
            .then(progress ->
                // STEP 1.0 [initialize a proxy context]
                // If your service API is not idempotent, meaning it does not distinguish duplicate create requests against some identifier (e.g; resource Name)
                // and instead returns a 200 even though a resource already exists, you must first check if the resource exists here
                // NOTE: If your service API throws 'ResourceAlreadyExistsException' for create requests this method is not necessary
                proxy.initiate("AWS-Kendra-FeaturedResultsSet::Create::PreExistanceCheck", proxyClient, model, progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadRequest)
                    .makeServiceCall(this::describeFeaturedResultsSet)
                    .handleError((describeFrsRequest, exception, client, resourceModel, context) -> {
                        // TODO: If frs ID is empty/null, may be a validation exception to catch instead
                         if (exception instanceof ResourceNotFoundException) {
                           return ProgressEvent.progress(resourceModel, context);
                         } else {
                           throw exception;
                         }
                    })
                    .progress()
            )

            // STEP 2 [create/stabilize progress chain - required for resource creation]
            .then(progress ->
                // If your service API throws 'ResourceAlreadyExistsException' for create requests then CreateHandler can return just proxy.initiate construction
                // STEP 2.0 [initialize a proxy context]
                // Implement client invocation of the create request through the proxyClient, which is already initialised with
                // caller credentials, correct region and retry settings
                proxy.initiate("AWS-Kendra-FeaturedResultsSet::Create", proxyClient, model, progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall(this::createFeaturedResultsSet)
                    .done(this::setId)
                )
            // STEP 3 [TODO: describe call/chain to return the resource model]
            .then(progress -> new ReadHandler(frsBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateFeaturedResultsSetResponse createFeaturedResultsSet(final CreateFeaturedResultsSetRequest createFrsRequest,
        final ProxyClient<KendraClient> proxyClient) {
        CreateFeaturedResultsSetResponse createFrsResponse;
        try {
          createFrsResponse = proxyClient.injectCredentialsAndInvokeV2(createFrsRequest, proxyClient.client()::createFeaturedResultsSet);
        } catch (ValidationException e) {
          throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (FeaturedResultsConflictException e) {
          throw new CfnResourceConflictException(e);
        } catch (final AwsServiceException e) {
          /*
           * While the handler contract states that the handler must always return a progress event,
           * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
           * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
           * to more specific error codes
           */
          throw new CfnGeneralServiceException(CREATE_FEATURED_RESULTS_SET, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return createFrsResponse;
    }

    private DescribeFeaturedResultsSetResponse describeFeaturedResultsSet(final DescribeFeaturedResultsSetRequest describeFrsRequest,
        final ProxyClient<KendraClient> proxyClient) {
      if (describeFrsRequest.featuredResultsSetId() == null) {
        // If resource model doesn't contain an FR ID, then we are making a new FR and do not need to check
        // for pre-existence. We will catch this error and progress.
        throw ResourceNotFoundException.builder().build();
      }
      DescribeFeaturedResultsSetResponse describeFrsResponse =
          proxyClient.injectCredentialsAndInvokeV2(describeFrsRequest, proxyClient.client()::describeFeaturedResultsSet);
      logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
      return describeFrsResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> setId(CreateFeaturedResultsSetRequest createRequest,
        CreateFeaturedResultsSetResponse createResponse,
        ProxyClient<KendraClient> proxyClient,
        ResourceModel resourceModel,
        CallbackContext callbackContext) {

      resourceModel.setFeaturedResultsSetId(createResponse.featuredResultsSet().featuredResultsSetId());
      return ProgressEvent.progress(resourceModel, callbackContext);
    }
}
