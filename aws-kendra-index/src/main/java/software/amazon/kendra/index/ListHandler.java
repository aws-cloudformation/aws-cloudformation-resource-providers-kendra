package software.amazon.kendra.index;

import static software.amazon.kendra.index.ApiName.LIST_INDICES;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ListIndicesRequest;
import software.amazon.awssdk.services.kendra.model.ListIndicesResponse;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<KendraClient> proxyClient,
            final Logger logger) {

        // STEP 1 [TODO: construct a body of a request]
        final ListIndicesRequest listIndicesRequest = Translator.translateToListRequest(request.getNextToken());

        // STEP 2 [TODO: make an api call]
        ListIndicesResponse listIndicesResponse = listIndices(listIndicesRequest, proxyClient);

        // STEP 3 [TODO: get a token for the next page]
        String nextToken = listIndicesResponse.nextToken();

        // STEP 4 [TODO: construct resource models]
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/master/aws-logs-loggroup/src/main/java/software/amazon/logs/loggroup/ListHandler.java#L19-L21

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListResponse(listIndicesResponse))
                .nextToken(nextToken)
                .status(OperationStatus.SUCCESS)
                .build();
    }

  private ListIndicesResponse listIndices (ListIndicesRequest listIndicesRequest, ProxyClient<KendraClient> proxyClient) {
    try {
      return proxyClient.injectCredentialsAndInvokeV2(listIndicesRequest, proxyClient.client()::listIndices);
    } catch (AccessDeniedException e) {
      throw new CfnAccessDeniedException(LIST_INDICES, e);
    } catch (ThrottlingException e) {
      throw new CfnThrottlingException(e);
    } catch (AwsServiceException e) {
      throw new CfnGeneralServiceException(LIST_INDICES, e);
    }
  }
}
