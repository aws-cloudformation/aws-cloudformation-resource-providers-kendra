package software.amazon.kendra.faq;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.DeleteFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.FaqSummary;
import software.amazon.awssdk.services.kendra.model.ListFaqsRequest;
import software.amazon.awssdk.services.kendra.model.ListFaqsResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TranslatorTest {

    @Test
    void testCreateFaqRequest() {
        String indexId = "indexId";
        String roleArn = "roleArn";
        String description = "description";
        String name = "name";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(s3Path)
                .roleArn(roleArn)
                .build();

        CreateFaqRequest createFaqRequest = CreateFaqRequest
                .builder()
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                        .builder()
                        .key(s3Key)
                        .bucket(s3Bucket)
                        .build())
                .roleArn(roleArn)
                .build();

        assertThat(Translator.translateToCreateRequest(resourceModel)).isEqualTo(createFaqRequest);
    }

    @Test
    void testDeleteFaqRequest() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        DeleteFaqRequest deleteFaqRequest = DeleteFaqRequest
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        assertThat(Translator.translateToDeleteRequest(resourceModel)).isEqualTo(deleteFaqRequest);
    }

    @Test
    void testReadFaqRequest() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        DescribeFaqRequest describeFaqRequest = DescribeFaqRequest
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        assertThat(Translator.translateToReadRequest(resourceModel)).isEqualTo(describeFaqRequest);
    }

    @Test
    void testReadFaqResponse() {
        String id = "id";
        String indexId = "indexId";
        String roleArn = "roleArn";
        String description = "description";
        String name = "name";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(s3Path)
                .roleArn(roleArn)
                .build();

        DescribeFaqResponse describeFaqResponse = DescribeFaqResponse
                .builder()
                .id(id)
                .indexId(indexId)
                .name(name)
                .roleArn(roleArn)
                .description(description)
                .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                        .builder()
                        .key(s3Key)
                        .bucket(s3Bucket)
                        .build())
                .build();
        assertThat(Translator.translateFromReadResponse(describeFaqResponse)).isEqualTo(resourceModel);
    }
    @Test
    void testTranslateToListRequest() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .indexId(indexId)
                .build();
        String nextToken = "nextToken";

        ListFaqsRequest listFaqsRequest = Translator.translateToListRequest(resourceModel, nextToken);
        assertThat(listFaqsRequest.indexId()).isEqualTo(indexId);
        assertThat(listFaqsRequest.nextToken()).isEqualTo(nextToken);
    }

    @Test
    void testTranslateFromListResponse() {
        String id1 = "id1";
        FaqSummary faqSummary1 = FaqSummary
                .builder()
                .id(id1)
                .build();
        String id2 = "id2";
        FaqSummary faqSummary2 = FaqSummary
                .builder()
                .id(id2)
                .build();
        ListFaqsResponse listFaqsResponse = ListFaqsResponse
                .builder()
                .faqSummaryItems(Arrays.asList(faqSummary1, faqSummary2))
                .build();
        List<ResourceModel> resourceModelList = Translator.translateFromListResponse(listFaqsResponse);
        assertThat(resourceModelList.size()).isEqualTo(2);
        assertThat(resourceModelList.get(0).getId()).isEqualTo(id1);
        assertThat(resourceModelList.get(1).getId()).isEqualTo(id2);
    }

}
