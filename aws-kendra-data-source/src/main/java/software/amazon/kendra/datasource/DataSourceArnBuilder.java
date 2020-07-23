package software.amazon.kendra.datasource;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public interface DataSourceArnBuilder {
    String build(ResourceHandlerRequest<ResourceModel> request);
}
