package software.amazon.kendra.index;

import java.util.Optional;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final Logger logger) {
    return handleRequest(
            proxy,
            request,
            callbackContext != null ? callbackContext : new CallbackContext(),
            proxy.newProxy(ClientBuilder::getClient),
            logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final ProxyClient<KendraClient> proxyClient,
          final Logger logger);

  protected boolean isCreatingOrUpdatingStable(
      final String operation,
      final ResourceHandlerRequest<ResourceModel> request,
      final ProxyClient<KendraClient> proxyClient,
      final ResourceModel model,
      final Logger logger
  ) {
    logger.log(
        String.format("[INFO] Checking completion of %s in stack: %s for Index : %s, For Account: %s",
            operation, request.getStackId(), model.getId(), request.getAwsAccountId())
    );

    DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
        .id(model.getId())
        .build();
    DescribeIndexResponse describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
        proxyClient.client()::describeIndex);
    IndexStatus indexStatus = describeIndexResponse.status();
    if (IndexStatus.FAILED.equals(indexStatus)) {
      // Check if there is an error message
      final RuntimeException indexFailError = Optional.ofNullable(describeIndexResponse.errorMessage())
          .map(RuntimeException::new)
          .orElse(null);
      if (indexFailError != null) {
        throw new CfnNotStabilizedException(indexFailError);
      }
      throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, model.getId());
    }

    boolean stabilized = indexStatus.equals(IndexStatus.ACTIVE);
    logger.log(String.format("%s [%s] %s has stabilized: %s",
        ResourceModel.TYPE_NAME, model.getPrimaryIdentifier(), operation, stabilized));
    return stabilized;
  }

}
