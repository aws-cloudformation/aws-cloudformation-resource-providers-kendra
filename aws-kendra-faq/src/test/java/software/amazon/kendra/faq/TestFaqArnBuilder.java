package software.amazon.kendra.faq;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TestFaqArnBuilder implements FaqArnBuilder {
    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return String.format("arn:aws:kendra:us-west-2:0123456789:index/%s/faq/%s",
                request.getDesiredResourceState().getId(),
                request.getDesiredResourceState().getIndexId());

    }
}
