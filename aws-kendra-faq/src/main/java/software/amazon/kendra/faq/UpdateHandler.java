package software.amazon.kendra.faq;

import com.google.common.collect.Sets;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    private FaqArnBuilder faqArnBuilder;

    public UpdateHandler() {
        super();
        faqArnBuilder = new FaqArn();
    }

    public UpdateHandler(FaqArnBuilder faqArnBuilder) {
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

        return ProgressEvent.progress(model, callbackContext)
                // STEP 1 [first update/stabilize progress chain - required for resource update]
                .then(progress -> updateTags(proxyClient, progress, request))
                .then(progress -> new ReadHandler(faqArnBuilder).handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
            final ProxyClient<KendraClient> proxyClient,
            final ProgressEvent<ResourceModel, CallbackContext> progress,
            ResourceHandlerRequest<ResourceModel> request) {
        ResourceModel resourceModel = progress.getResourceModel();
        CallbackContext callbackContext = progress.getCallbackContext();
        Set<Tag> currentTags = new HashSet<>();
        if (resourceModel.getTags() != null) {
            currentTags = resourceModel.getTags().stream().collect(Collectors.toSet());
        }

        String arn = faqArnBuilder.build(request);
        ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsRequest(arn);
        List<software.amazon.awssdk.services.kendra.model.Tag> existingTagsSdk = proxyClient.injectCredentialsAndInvokeV2(
                listTagsForResourceRequest, proxyClient.client()::listTagsForResource).tags();
        Set<Tag> existingTags = existingTagsSdk.stream().map(x -> Tag.builder().key(x.key()).value(x.value()).build())
                .collect(Collectors.toSet());

        final Set<Tag> tagsToAdd = Sets.difference(currentTags, existingTags);
        if (!tagsToAdd.isEmpty()) {
            TagResourceRequest tagResourceRequest = Translator.translateToTagResourceRequest(tagsToAdd, arn);
            try {
                proxyClient.injectCredentialsAndInvokeV2(tagResourceRequest, proxyClient.client()::tagResource);
            } catch (ValidationException e) {
                throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME, e);
            }
        }

        final Set<Tag> tagsToRemove = Sets.difference(existingTags, currentTags);
        if (!tagsToRemove.isEmpty()) {
            UntagResourceRequest untagResourceRequest = Translator.translateToUntagResourceRequest(tagsToRemove, arn);
            try {
                proxyClient.injectCredentialsAndInvokeV2(untagResourceRequest, proxyClient.client()::untagResource);
            } catch (ValidationException e) {
                throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME, e);
            }
        }

        return ProgressEvent.progress(resourceModel, callbackContext);
    }


}
