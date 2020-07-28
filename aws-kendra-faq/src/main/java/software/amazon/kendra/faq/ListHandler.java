package software.amazon.kendra.faq;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ListFaqsRequest;
import software.amazon.awssdk.services.kendra.model.ListFaqsResponse;
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

        ResourceModel resourceModel = request.getDesiredResourceState();
        // STEP 1 [TODO: construct a body of a request]
        final ListFaqsRequest listFaqsRequest = Translator.translateToListRequest(resourceModel, request.getNextToken());
        // STEP 2 [TODO: make an api call]
        ListFaqsResponse listFaqsResponse = proxy.injectCredentialsAndInvokeV2(listFaqsRequest, proxyClient.client()::listFaqs);
        // STEP 3 [TODO: get a token for the next page]
        String nextToken = listFaqsResponse.nextToken();
        // STEP 4 [TODO: construct resource models]
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/master/aws-logs-loggroup/src/main/java/software/amazon/logs/loggroup/ListHandler.java#L19-L21

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListResponse(listFaqsResponse, resourceModel.getIndexId()))
                .nextToken(nextToken)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
