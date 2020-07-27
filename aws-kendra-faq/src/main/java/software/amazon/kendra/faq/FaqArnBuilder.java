package software.amazon.kendra.faq;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public interface FaqArnBuilder {
    String build(ResourceHandlerRequest<ResourceModel> request);
}
