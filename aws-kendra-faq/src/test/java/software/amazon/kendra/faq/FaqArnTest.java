package software.amazon.kendra.faq;

import org.junit.jupiter.api.Test;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FaqArnTest {

    @Test
    public void testBuild() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String faqId = "faqId";
        String indexId = "indexId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .build());
        assertThat(new FaqArn().build(request))
                .isEqualTo("arn:aws:kendra:us-west-2:0123456789:index/indexId/faq/faqId");
    }

    @Test
    public void testBuildThrowsExceptionWhenPartitionIsNull() {
        String region = "us-west-2";
        String accountId = "0123456789";
        String faqId = "faqId";
        String indexId = "indexId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(null);
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder()
                .id(faqId)
                .indexId(indexId)
                .build());
        assertThrows(NullPointerException.class, () -> {
            new FaqArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenRegionIsNull() {
        String partition = "aws";
        String accountId = "0123456789";
        String faqId = "faqId";
        String indexId = "indexId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setRegion(null);
        request.setDesiredResourceState(ResourceModel.builder()
                .id(faqId)
                .indexId(indexId)
                .build());
        assertThrows(NullPointerException.class, () -> {
            new FaqArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenAccountIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String faqId = "0123456789abcdef";
        String indexId = "indexId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(null);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder()
                .id(faqId)
                .indexId(indexId)
                .build());
        assertThrows(NullPointerException.class, () -> {
            new FaqArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenFaqIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String indexId = "indexId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder()
                .id(null)
                .indexId(indexId)
                .build());
        assertThrows(NullPointerException.class, () -> {
            new FaqArn().build(request);
        });
    }

    @Test
    public void testBuildThrowsExceptionWhenIndexIdIsNull() {
        String partition = "aws";
        String region = "us-west-2";
        String accountId = "0123456789";
        String faqId = "faqId";
        ResourceHandlerRequest<ResourceModel> request = new ResourceHandlerRequest<>();
        request.setAwsPartition(partition);
        request.setAwsAccountId(accountId);
        request.setRegion(region);
        request.setDesiredResourceState(ResourceModel.builder()
                .id(faqId)
                .indexId(null)
                .build());
        assertThrows(NullPointerException.class, () -> {
            new FaqArn().build(request);
        });
    }


}
