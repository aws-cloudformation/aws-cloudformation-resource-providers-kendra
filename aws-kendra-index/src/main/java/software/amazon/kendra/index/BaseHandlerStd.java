package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.function.BiFunction;
import java.util.function.Function;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

  protected static final BiFunction<ResourceModel, ProxyClient<KendraClient>, ResourceModel> EMPTY_CALL =
          (model, proxyClient) -> model;
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

  protected ProgressEvent<ResourceModel, CallbackContext> waitForIndexToBeStable(
          final AmazonWebServicesClientProxy proxy,
          final ProxyClient<KendraClient> proxyClient,
          final ProgressEvent<ResourceModel, CallbackContext> progress) {
    return proxy.initiate("AWS-Kendra-Index::stabilize", proxyClient, progress.getResourceModel(),
            progress.getCallbackContext())
            .translateToServiceRequest(Function.identity())
            .makeServiceCall(EMPTY_CALL)
            .stabilize((resourceModel, response, proxyInvocation, model, callbackContext) ->
                    isStabilized(proxyInvocation, model)).progress();

  }

  private boolean isStabilized(final ProxyClient<KendraClient> proxyClient, final ResourceModel model) {
    DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
            .id(model.getId())
            .build();
    DescribeIndexResponse describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(describeIndexRequest,
            proxyClient.client()::describeIndex);
    final boolean stabilized = describeIndexResponse.status().equals(IndexStatus.ACTIVE);
    return stabilized;
  }

}
