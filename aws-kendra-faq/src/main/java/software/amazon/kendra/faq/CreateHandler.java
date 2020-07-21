package software.amazon.kendra.faq;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.CreateFaqResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.FaqStatus;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CreateHandler extends BaseHandlerStd {
    protected static final BiFunction<ResourceModel, ProxyClient<KendraClient>, ResourceModel> EMPTY_CALL =
            (model, proxyClient) -> model;

    private Logger logger;

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

        return ProgressEvent.progress(model, callbackContext)

                // STEP 1 [check if resource already exists]
                // if target API does not support 'ResourceAlreadyExistsException' then following check is required
                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
                //.then(progress -> checkForPreCreateResourceExistence(proxy, request, progress))

                // STEP 2 [create/stabilize progress chain - required for resource creation]
                .then(progress ->
                        // If your service API throws 'ResourceAlreadyExistsException' for create requests then CreateHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        proxy.initiate("AWS-Kendra-Faq::Create", proxyClient, model, callbackContext)
                                // STEP 2.1 [TODO: construct a body of a request]
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall(this::createResource)
                                .done(this::setId)
                )
                .then(progress -> stabilize(proxy, proxyClient, progress))
                // STEP 4 [TODO: describe call/chain to return the resource model]
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> setId(CreateFaqRequest createFaqRequest,
                                                                CreateFaqResponse createFaqResponse,
                                                                ProxyClient<KendraClient> proxyClient,
                                                                ResourceModel resourceModel,
                                                                CallbackContext callbackContext) {

        resourceModel.setId(createFaqResponse.id());
        return ProgressEvent.progress(resourceModel, callbackContext);
    }

    private ProgressEvent<ResourceModel, CallbackContext> stabilize(
            final AmazonWebServicesClientProxy proxy,
            final ProxyClient<KendraClient> proxyClient,
            final ProgressEvent<ResourceModel, CallbackContext> progress) {
        return proxy.initiate("AWS-Kendra-Faq::PostCreateStabilize", proxyClient, progress.getResourceModel(),
                progress.getCallbackContext())
                .translateToServiceRequest(Function.identity())
                .makeServiceCall(EMPTY_CALL)
                .stabilize((request, response, proxyInvocation, model, callbackContext) ->
                        isStabilized(proxyInvocation, model)).progress();

    }

    private boolean isStabilized(final ProxyClient<KendraClient> proxyClient, final ResourceModel model) {
        DescribeFaqRequest describeFaqRequest = DescribeFaqRequest.builder()
                .id(model.getId())
                .build();
        DescribeFaqResponse describeFaqResponse = proxyClient.injectCredentialsAndInvokeV2(describeFaqRequest,
                proxyClient.client()::describeFaq);
        FaqStatus faqStatus = describeFaqResponse.status();
        if (faqStatus.equals(FaqStatus.FAILED)) {
            throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, model.getId());
        }
        return faqStatus.equals(FaqStatus.ACTIVE);
    }

    /**
     * Implement client invocation of the create request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param awsRequest the aws service request to create a resource
     * @param proxyClient the aws service client to make the call
     * @return awsResponse create resource response
     */
    private CreateFaqResponse createResource(
            final CreateFaqRequest createFaqRequest,
            final ProxyClient<KendraClient> proxyClient) {
        CreateFaqResponse createFaqResponse;
        try {
            createFaqResponse = proxyClient.injectCredentialsAndInvokeV2(createFaqRequest, proxyClient.client()::createFaq);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return createFaqResponse;
    }
}
