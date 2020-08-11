package software.amazon.kendra.datasource;

import org.junit.jupiter.api.Test;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataSourceArnTest {

    @Test
    public void testBuild() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String indexId = "0123456789index";
        String dataSourceId = "0123456789dataSource";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder().id(dataSourceId).indexId(indexId).build());
        assertThat(new DataSourceArn().build(request))
                .isEqualTo("arn:aws:kendra:us-west-2:0123456789:index/0123456789index/data-source/0123456789dataSource");
    }

    @Test
    public void testBuildThrowsExceptionWhenPartitionIsNull() {
        String region = "us-west-2";
        String accountId = "0123456789";
        String indexId = "0123456789index";
        String dataSourceId = "0123456789dataSource";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder().id(dataSourceId).indexId(indexId).build());
        assertThrows(NullPointerException.class, () -> {
            new DataSourceArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenRegionIsNull() {
        String partition = "aws";
        String accountId = "0123456789";
        String indexId = "0123456789index";
        String dataSourceId = "0123456789dataSource";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setDesiredResourceState(ResourceModel.builder().id(dataSourceId).indexId(indexId).build());
        assertThrows(NullPointerException.class, () -> {
            new DataSourceArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenAccountIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String indexId = "0123456789index";
        String dataSourceId = "0123456789dataSource";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder().id(dataSourceId).indexId(indexId).build());
        assertThrows(NullPointerException.class, () -> {
            new DataSourceArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenDataSourceIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String indexId = "0123456789index";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setRegion(region);
        request.setAwsAccountId(accountId);
        request.setDesiredResourceState(ResourceModel.builder().id(null).indexId(indexId).build());
        assertThrows(NullPointerException.class, () -> {
            new DataSourceArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenIndexIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String dataSourceId = "0123456789dataSource";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setRegion(region);
        request.setAwsAccountId(accountId);
        request.setDesiredResourceState(ResourceModel.builder().id(dataSourceId).indexId(null).build());
        assertThrows(NullPointerException.class, () -> {
            new DataSourceArn().build(request);
        });
    }

}
