package software.amazon.kendra.datasource;

import lombok.NonNull;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DataSourceArn implements DataSourceArnBuilder {
    // See https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazonkendra.html#amazonkendra-resources-for-iam-policies.
    // arn:${Partition}:kendra:${Region}:${Account}:index/${IndexId}/data-source/${DataSourceId}
    private String dataSourceArnFormat = "arn:%s:kendra:%s:%s:index/%s/data-source/%s";

    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return build(request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
         request.getDesiredResourceState().getIndexId(), request.getDesiredResourceState().getId());
    }

    private String build(@NonNull String partition, @NonNull String region, @NonNull String accountId,
      @NonNull String indexId, @NonNull String dataSourceId) {
        return String.format(dataSourceArnFormat, partition, region, accountId, indexId, dataSourceId);
    }

}
