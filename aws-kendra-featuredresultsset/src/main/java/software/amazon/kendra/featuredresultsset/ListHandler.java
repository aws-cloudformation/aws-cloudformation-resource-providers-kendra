package software.amazon.kendra.featuredresultsset;

import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsRequest;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        // STEP 1 [TODO: construct a body of a request]
        final ListFeaturedResultsSetsRequest listRequest = Translator.translateToListRequest(request.getDesiredResourceState(), request.getNextToken());

        // STEP 2 [TODO: make an api call]
        ListFeaturedResultsSetsResponse listResponse = proxy.injectCredentialsAndInvokeV2(listRequest, proxyClient.client()::listFeaturedResultsSets);

        // STEP 3 [TODO: get a token for the next page]
        String nextToken = listResponse.nextToken();

        // STEP 4 [TODO: construct resource models]
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/master/aws-logs-loggroup/src/main/java/software/amazon/logs/loggroup/ListHandler.java#L19-L21

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListResponse(listResponse, request.getDesiredResourceState().getIndexId()))
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
