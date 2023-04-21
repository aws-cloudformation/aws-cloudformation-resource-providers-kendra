package software.amazon.kendra.featuredresultsset;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public interface FeaturedResultsArnBuilder {
  String build(ResourceHandlerRequest<ResourceModel> request);
}
