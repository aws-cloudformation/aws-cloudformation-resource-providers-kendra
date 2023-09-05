package software.amazon.kendra.faq;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceInUseException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.kendra.faq.ApiName.DESCRIBE_FAQ;
import static software.amazon.kendra.faq.ApiName.LIST_TAGS_FOR_RESOURCE;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;
    private FaqArnBuilder faqArnBuilder;

    public ReadHandler() {
        super();
        faqArnBuilder = new FaqArn();
    }

    public ReadHandler(FaqArnBuilder faqArnBuilder) {
        this.faqArnBuilder = faqArnBuilder;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<KendraClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        DescribeFaqRequest describeFaqRequest = Translator.translateToReadRequest(model);
        DescribeFaqResponse describeFaqResponse = readFaq(describeFaqRequest, proxyClient);
        String faqArn = faqArnBuilder.build(request);
        final ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsRequest(faqArn);
        ListTagsForResourceResponse listTagsForResourceResponse;
        try {
            listTagsForResourceResponse = proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest,
                    proxyClient.client()::listTagsForResource);
        } catch (ResourceInUseException e) {
            throw new CfnGeneralServiceException(LIST_TAGS_FOR_RESOURCE, e);
        }
        return constructResourceModelFromResponse(describeFaqResponse, listTagsForResourceResponse, faqArn);
    }

    /**
     * Implement client invocation of the read request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param describeFaqRequest the aws service request to describe a resource
     * @param proxyClient the aws service client to make the call
     * @return describe resource response
     */
    private DescribeFaqResponse readFaq(
            final DescribeFaqRequest describeFaqRequest,
            final ProxyClient<KendraClient> proxyClient) {
        DescribeFaqResponse describeFaqResponse;
        try {
            describeFaqResponse = proxyClient.injectCredentialsAndInvokeV2(
                    describeFaqRequest, proxyClient.client()::describeFaq);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeFaqRequest.id(), e);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(DESCRIBE_FAQ, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(DESCRIBE_FAQ, e);
        } catch (final AwsServiceException e) { // ResourceNotFoundException
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(DESCRIBE_FAQ, e); // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/commit/2077c92299aeb9a68ae8f4418b5e932b12a8b186#diff-5761e3a9f732dc1ef84103dc4bc93399R56-R63
        }

        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
        return describeFaqResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final DescribeFaqResponse describeFaqResponse,
            final ListTagsForResourceResponse listTagsForResourceResponse,
            final String arn) {
        return ProgressEvent.defaultSuccessHandler(
                Translator.translateFromReadResponse(
                        describeFaqResponse,
                        listTagsForResourceResponse,
                        arn));
    }
}
