package software.amazon.kendra.faq;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.DeleteFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;

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

}
