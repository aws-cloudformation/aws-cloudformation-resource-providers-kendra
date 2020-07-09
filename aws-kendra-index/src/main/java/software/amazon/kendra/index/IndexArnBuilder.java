package software.amazon.kendra.index;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public interface IndexArnBuilder {
    String build(ResourceHandlerRequest<ResourceModel> request);
}
