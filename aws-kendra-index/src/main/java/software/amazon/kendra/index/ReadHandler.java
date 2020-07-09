package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;

    private IndexArnBuilder indexArnBuilder;

    public ReadHandler() {
        super();
        this.indexArnBuilder = new IndexArn();
    }

    public ReadHandler(IndexArnBuilder indexArnBuilder) {
        this.indexArnBuilder = indexArnBuilder;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();

        final DescribeIndexRequest describeIndexRequest = Translator.translateToReadRequest(model);
        DescribeIndexResponse describeIndexResponse;
        try {
            describeIndexResponse = proxyClient.injectCredentialsAndInvokeV2(
                    describeIndexRequest, proxyClient.client()::describeIndex);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeIndexRequest.id(), e);
        } catch (final AwsServiceException e) { // ResourceNotFoundException
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e); // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/commit/2077c92299aeb9a68ae8f4418b5e932b12a8b186#diff-5761e3a9f732dc1ef84103dc4bc93399R56-R63
        }

        String indexArn = indexArnBuilder.build(request);
        final ListTagsForResourceRequest listTagsForResourceRequest =
                Translator.translateToListTagsRequest(indexArn);
        ListTagsForResourceResponse listTagsForResourceResponse = proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest,
                proxyClient.client()::listTagsForResource);

        return constructResourceModelFromResponse(describeIndexResponse, listTagsForResourceResponse, indexArn);
    }

    /**
     * Implement client invocation of the read request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param describeIndexResponse the aws service describe resource response
     * @return progressEvent indicating success, in progress with delay callback or failed state
     */
    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final DescribeIndexResponse describeIndexResponse,
            final ListTagsForResourceResponse listTagsForResourceResponse,
            String indexArn) {
        ResourceModel resourceModel = Translator.translateFromReadResponse(describeIndexResponse, listTagsForResourceResponse, indexArn);
        return ProgressEvent.defaultSuccessHandler(resourceModel);
    }
}
