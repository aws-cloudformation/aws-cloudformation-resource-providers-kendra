package software.amazon.kendra.datasource;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesResponse;
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

        final String indexId = request.getDesiredResourceState().getIndexId();

        final ListDataSourcesRequest listDataSourcesRequest = Translator.translateToListRequest(
            indexId,request.getNextToken());

        ListDataSourcesResponse listDataSourcesResponse = proxy.injectCredentialsAndInvokeV2(listDataSourcesRequest, proxyClient.client()::listDataSources);

        String nextToken = listDataSourcesResponse.nextToken();

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListResponse(listDataSourcesResponse, indexId))
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
