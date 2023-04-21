package software.amazon.kendra.featuredresultsset;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TestFeaturedResultsArn implements FeaturedResultsArnBuilder {
  @Override
  public String build(ResourceHandlerRequest<ResourceModel> request) {
    return String.format("arn:aws:kendra:us-west-2:0123456789:index/%s/featured-results-set/%s",
        request.getDesiredResourceState().getIndexId(),
        request.getDesiredResourceState().getFeaturedResultsSetId());
  }
}
