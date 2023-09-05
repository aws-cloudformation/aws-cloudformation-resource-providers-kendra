package software.amazon.kendra.faq;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.CreateFaqResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.FaqStatus;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.function.BiFunction;
import java.util.function.Function;

import static software.amazon.kendra.faq.ApiName.CREATE_FAQ;

public class CreateHandler extends BaseHandlerStd {
    protected static final BiFunction<ResourceModel, ProxyClient<KendraClient>, ResourceModel> EMPTY_CALL =
            (model, proxyClient) -> model;

    private Logger logger;

    private final FaqArnBuilder faqArnBuilder;

    public CreateHandler() {
        super();
        faqArnBuilder = new FaqArn();
    }

    public CreateHandler(FaqArnBuilder faqArnBuilder) {
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

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-Kendra-Faq::Create", proxyClient, model, callbackContext)
                                // STEP 2.1 [TODO: construct a body of a request]
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall(this::createFaq)
                                .done(this::setId)
                )
                .then(progress -> stabilize(proxy, proxyClient, progress))
                .then(progress -> new ReadHandler(faqArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
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
        DescribeFaqRequest describeFaqRequest = Translator.translateToReadRequest(model);
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
     * @param createFaqRequest the aws service request to create a resource
     * @param proxyClient the aws service client to make the call
     * @return awsResponse create resource response
     */
    private CreateFaqResponse createFaq(
            final CreateFaqRequest createFaqRequest,
            final ProxyClient<KendraClient> proxyClient) {
        CreateFaqResponse createFaqResponse;
        try {
            createFaqResponse = proxyClient.injectCredentialsAndInvokeV2(createFaqRequest, proxyClient.client()::createFaq);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e.getMessage(), e);
        } catch (ConflictException e) {
            throw new CfnResourceConflictException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (ServiceQuotaExceededException e) {
            throw new CfnServiceLimitExceededException(ResourceModel.TYPE_NAME, e.getMessage(), e.getCause());
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(CREATE_FAQ, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(CREATE_FAQ, e);
        } catch (final AwsServiceException e) {
            /*
             * While the handler contract states that the handler must always return a progress event,
             * you may throw any instance of BaseHandlerException, as the wrapper map it to a progress event.
             * Each BaseHandlerException maps to a specific error code, and you should map service exceptions as closely as possible
             * to more specific error codes
             */
            throw new CfnGeneralServiceException(CREATE_FAQ, e);
        }

        logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
        return createFaqResponse;
    }
}
