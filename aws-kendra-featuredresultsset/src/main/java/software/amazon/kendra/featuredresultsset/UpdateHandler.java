package software.amazon.kendra.featuredresultsset;

import static software.amazon.kendra.featuredresultsset.ApiName.CREATE_FEATURED_RESULTS_SET;
import static software.amazon.kendra.featuredresultsset.ApiName.UPDATE_FEATURED_RESULTS_SET;

import java.util.Optional;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsConflictException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;
    private FeaturedResultsArnBuilder frsBuilder;

    public UpdateHandler() {
      super();
      frsBuilder = new FeaturedResultsArn();
    }

    // Used for testing.
    public UpdateHandler(FeaturedResultsArnBuilder frsBuilder) {
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

        verifyNonUpdatableFields(model, request.getPreviousResourceState());

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)

            // STEP 1 [check if resource already exists]
            // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
            // if target API does not support 'ResourceNotFoundException' then following check is required
            .then(progress ->
                // STEP 1.0 [initialize a proxy context]
                // If your service API does not return ResourceNotFoundException on update requests against some identifier (e.g; resource Name)
                // and instead returns a 200 even though a resource does not exist, you must first check if the resource exists here
                // NOTE: If your service API throws 'ResourceNotFoundException' for update requests this method is not necessary
                proxy.initiate("AWS-Kendra-FeaturedResultsSet::Update::PreUpdateCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToReadRequest)
                    .makeServiceCall(this::validateResourceExists)
                    .progress()
            )
            .then(progress ->
                // STEP 2.0 [initialize a proxy context]
                // Implement client invocation of the update request through the proxyClient, which is already initialised with
                // caller credentials, correct region and retry settings
                proxy.initiate("AWS-Kendra-FeaturedResultsSet::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToUpdateRequest)
                    .makeServiceCall(this::updateFeaturedResultsSet)
                    .progress())
            .then(progress -> new ReadHandler(frsBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private DescribeFeaturedResultsSetResponse validateResourceExists(final DescribeFeaturedResultsSetRequest describeFrsRequest,
        final ProxyClient<KendraClient> proxyClient) {
        DescribeFeaturedResultsSetResponse describeFrsResponse;
        try {
          describeFrsResponse = proxyClient.injectCredentialsAndInvokeV2(describeFrsRequest, proxyClient.client()::describeFeaturedResultsSet);
        } catch (ResourceNotFoundException e) {
          throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeFrsRequest.featuredResultsSetId(), e);
        }
        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
        return describeFrsResponse;
    }

    private UpdateFeaturedResultsSetResponse updateFeaturedResultsSet(final UpdateFeaturedResultsSetRequest updateFrsRequest,
        final ProxyClient<KendraClient> proxyClient) {
        UpdateFeaturedResultsSetResponse updateFrsResponse;
        try {
          updateFrsResponse = proxyClient.injectCredentialsAndInvokeV2(updateFrsRequest, proxyClient.client()::updateFeaturedResultsSet);
        } catch (ValidationException e) {
          throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (FeaturedResultsConflictException e) {
          throw new CfnResourceConflictException(e);
        } catch (ResourceNotFoundException e) {
          throw new CfnNotFoundException(ResourceModel.TYPE_NAME, updateFrsRequest.featuredResultsSetId(), e);
        } catch (final AwsServiceException e) {
          /*
           * While the handler contract states that the handler must always return a progress event,
           * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
           * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
           * to more specific error codes
           */
          throw new CfnGeneralServiceException(UPDATE_FEATURED_RESULTS_SET, e);
        }

        logger.log(String.format("%s successfully updated.", ResourceModel.TYPE_NAME));
        return updateFrsResponse;
    }

    private void verifyNonUpdatableFields(ResourceModel currModel, ResourceModel prevModel) {
        if (prevModel != null) {
          if (!Optional.ofNullable(currModel.getIndexId()).equals(Optional.ofNullable(prevModel.getIndexId()))) {
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "IndexId");
          }
          if (!Optional.ofNullable(currModel.getFeaturedResultsSetId()).equals(
              Optional.ofNullable(prevModel.getFeaturedResultsSetId()))) {
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "FeaturedResultsSetId");
          }
        }
    }
}
