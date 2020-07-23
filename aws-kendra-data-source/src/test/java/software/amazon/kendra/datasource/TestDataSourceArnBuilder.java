package software.amazon.kendra.datasource;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TestDataSourceArnBuilder implements DataSourceArnBuilder{
    @Override
    public String build(ResourceHandlerRequest<ResourceModel> request) {
        return String.format("arn:aws:kendra:us-west-2:0123456789:index/%s/data-source/%s",
          request.getDesiredResourceState().getIndexId(), request.getDesiredResourceState().getId());
    }
}
