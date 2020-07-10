package software.amazon.kendra.index;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TestIndexArnBuilder implements IndexArnBuilder {

    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return String.format("arn:aws:kendra:us-west-2:0123456789:index/%s", request.getDesiredResourceState().getId());
    }
}
