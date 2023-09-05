package software.amazon.kendra.featuredresultsset;

import static software.amazon.kendra.featuredresultsset.ApiName.BATCH_DELETE_FEATURED_RESULTS_SETS;
import static software.amazon.kendra.featuredresultsset.ApiName.CREATE_FEATURED_RESULTS_SET;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.ErrorCode;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
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

public class DeleteHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)

            // STEP 1 [check if resource already exists]
            .then(progress -> preExistenceCheckForDelete(proxy, proxyClient, progress, request))
            .then(progress ->
                proxy.initiate("AWS-Kendra-FeaturedResultsSet::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall(this::deleteFeaturedResultsSet)
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

      DescribeFeaturedResultsSetRequest describeFrsRequest = Translator.translateToReadRequest(model);
      try {
        proxyClient.injectCredentialsAndInvokeV2(describeFrsRequest, proxyClient.client()::describeFeaturedResultsSet);
        return ProgressEvent.progress(model, callbackContext);
      } catch (ResourceNotFoundException e) {
        logger.log(String.format("%s [%s] does not pre-exist", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeFrsRequest.featuredResultsSetId(), e);
      }
    }

    private BatchDeleteFeaturedResultsSetResponse deleteFeaturedResultsSet(final BatchDeleteFeaturedResultsSetRequest deleteRequest,
        final ProxyClient<KendraClient> proxyClient) {
        BatchDeleteFeaturedResultsSetResponse deleteResponse;
        try {
          deleteResponse = proxyClient.injectCredentialsAndInvokeV2(deleteRequest, proxyClient.client()::batchDeleteFeaturedResultsSet);
        } catch (ValidationException e) {
          throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (AwsServiceException e) {
          if (e instanceof ResourceNotFoundException) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, deleteRequest.featuredResultsSetIds()
                .get(0), e);
          } else {
            throw new CfnGeneralServiceException(BATCH_DELETE_FEATURED_RESULTS_SETS, e);
          }
        }

        // Since this is a batch delete API, check for individual errors. InvalidRequest means the FR ID was not found.
        deleteResponse.errors().forEach(error -> {
          if (error.errorCode().equals(ErrorCode.INVALID_REQUEST)) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, error.id());
          }
        });

        logger.log(String.format("%s successfully deleted.", ResourceModel.TYPE_NAME));
        return deleteResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> setResourceModelToNullAndReturnSuccess(
        BatchDeleteFeaturedResultsSetRequest deleteRequest,
        BatchDeleteFeaturedResultsSetResponse deleteResponse,
        ProxyClient<KendraClient> proxyClient,
        ResourceModel resourceModel,
        CallbackContext callbackContext) {
      return ProgressEvent.defaultSuccessHandler(null);
    }
}
