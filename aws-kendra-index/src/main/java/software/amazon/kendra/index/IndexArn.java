package software.amazon.kendra.index;

import lombok.NonNull;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class IndexArn implements IndexArnBuilder {

    // See https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazonkendra.html#amazonkendra-resources-for-iam-policies.
    // arn:${Partition}:kendra:${Region}:${Account}:index/${IndexId}
    private String indexArnFormat = "arn:%s:kendra:%s:%s:index/%s";

    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return build(request.getAwsPartition(), request.getRegion(),
                request.getAwsAccountId(), request.getDesiredResourceState().getId());
    }

    private String build(@NonNull String partition, @NonNull String region,
                         @NonNull String accountId, @NonNull String indexId) {
        return String.format(indexArnFormat, partition, region, accountId, indexId);
    }

}
