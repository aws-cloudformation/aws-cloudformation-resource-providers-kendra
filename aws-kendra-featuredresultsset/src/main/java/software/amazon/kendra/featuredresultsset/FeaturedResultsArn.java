package software.amazon.kendra.featuredresultsset;

import lombok.NonNull;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class FeaturedResultsArn implements FeaturedResultsArnBuilder {
  // See https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazonkendra.html#amazonkendra-resources-for-iam-policies.
  private String featuredResultsArnFormat = "arn:%s:kendra:%s:%s:index/%s/featured-results-set/%s";

  @Override
  public String build(ResourceHandlerRequest<ResourceModel> request) {
    return build(request.getAwsPartition(), request.getRegion(),
        request.getAwsAccountId(), request.getDesiredResourceState().getIndexId(),
        request.getDesiredResourceState().getFeaturedResultsSetId());
  }

  private String build(@NonNull String partition, @NonNull String region,
      @NonNull String accountId, @NonNull String indexId,
      @NonNull String featuredResultsSetId) {
    return String.format(featuredResultsArnFormat, partition, region, accountId, indexId, featuredResultsSetId);
  }
}
