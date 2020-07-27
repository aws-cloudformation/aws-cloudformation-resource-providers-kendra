package software.amazon.kendra.faq;

import lombok.NonNull;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class FaqArn implements FaqArnBuilder {
    // See https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazonkendra.html#amazonkendra-resources-for-iam-policies.
    private String faqArnFormat = "arn:%s:kendra:%s:%s:index/%s/faq/%s";

    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return build(request.getAwsPartition(), request.getRegion(),
                request.getAwsAccountId(), request.getDesiredResourceState().getIndexId(),
                request.getDesiredResourceState().getId());
    }

    private String build(@NonNull String partition, @NonNull String region,
                         @NonNull String accountId, @NonNull String indexId,
                         @NonNull String faqId) {
        return String.format(faqArnFormat, partition, region, accountId, indexId, faqId);
    }
}
