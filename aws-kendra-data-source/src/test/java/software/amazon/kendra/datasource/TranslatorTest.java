package software.amazon.kendra.datasource;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceRequest;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import software.amazon.awssdk.services.kendra.model.DataSourceType;

import javax.sql.DataSource;

public class TranslatorTest {

    @Test
    void testTranslateToReadRequest() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        DescribeDataSourceRequest describeDataSourceRequest = Translator.translateToReadRequest(resourceModel);
        assertThat(describeDataSourceRequest.id()).isEqualTo(id);
        assertThat(describeDataSourceRequest.indexId()).isEqualTo(indexId);
    }

    @Test
    void testTranslateToDeleteRequest() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .build();
        DeleteDataSourceRequest deleteIndexRequest = Translator.translateToDeleteRequest(resourceModel);
        assertThat(deleteIndexRequest.id()).isEqualTo(id);
        assertThat(deleteIndexRequest.indexId()).isEqualTo(indexId);
    }

    @Test
    void testTranslateToCreateRequest_WithS3DataSource() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .type("S3")
            .dataSourceConfiguration(DataSourceConfiguration.builder()
                .s3Configuration(S3DataSourceConfiguration.builder().build())
                .build())
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.configuration().s3Configuration()).isNotNull();
    }

    @Test
    void testTranslateToCreateRequest_WithSharePointDataSource() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .type("SHAREPOINT")
            .dataSourceConfiguration(DataSourceConfiguration.builder()
                .sharePointConfiguration(SharePointConfiguration.builder().build())
                .build())
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.configuration().sharePointConfiguration()).isNotNull();
    }

    @Test
    void testTranslateToCreateRequest_WithInvalidDataSource() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .description("description")
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.configuration()).isEqualTo(null);
    }

    @Test
    void testTranslateToCreateRequest_WithTags() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .tags(Arrays.asList(Tag.builder().key("key").value("value").build()))
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.tags().size()).isEqualTo(1);
        assertThat(createDataSourceRequest.tags().get(0).key()).isEqualTo("key");
        assertThat(createDataSourceRequest.tags().get(0).value()).isEqualTo("value");
    }

    @Test
    void testTranslateToUpdateRequest() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .id(id)
            .indexId(indexId)
            .description("description")
            .build();
        UpdateDataSourceRequest updateDataSourceRequest = Translator.translateToUpdateRequest(resourceModel);
        assertThat(updateDataSourceRequest.id()).isEqualTo(id);
        assertThat(updateDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(updateDataSourceRequest.description()).isEqualTo("description");
        assertThat(updateDataSourceRequest.name()).isEqualTo("");
        assertThat(updateDataSourceRequest.roleArn()).isEqualTo("");
        assertThat(updateDataSourceRequest.schedule()).isEqualTo("");
        assertThat(updateDataSourceRequest.configuration())
            .isEqualTo(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder().build());
    }

    @Test
    void testTranslateToListRequest()  {
        String nextToken = "nextToken";
        String indexId = "indexId";
        ListDataSourcesRequest listDataSourcesRequest = Translator.translateToListRequest(indexId, nextToken);
        assertThat(listDataSourcesRequest.indexId()).isEqualTo(indexId);
        assertThat(listDataSourcesRequest.nextToken()).isEqualTo(nextToken);
    }

    @Test
    void testTranslateToUntagResourceRequest() {
        String arn = "arn";
        String key = "key";
        String value = "value";
        Tag tag = Tag.builder().key(key).value(value).build();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(tag);
        UntagResourceRequest actual = Translator.translateToUntagResourceRequest(tags, arn);
        UntagResourceRequest expected = UntagResourceRequest
                .builder()
                .resourceARN(arn)
                .tagKeys(Arrays.asList(key))
                .build();
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    void testTranslateToTagResourceRequest() {
        String arn = "arn";
        String key = "key";
        String value = "value";
        Tag tag = Tag.builder().key(key).value(value).build();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(tag);
        TagResourceRequest actual = Translator.translateToTagResourceRequest(tags, arn);
        TagResourceRequest expected = TagResourceRequest
                .builder()
                .resourceARN(arn)
                .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                        .builder()
                        .key(key)
                        .value(value)
                        .build()))
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testTranslateS3() {
        ResourceModel resourceModel = ResourceModel
                .builder()
                .type("S3")
                .dataSourceConfiguration(DataSourceConfiguration
                        .builder()
                        .s3Configuration(S3DataSourceConfiguration.builder().build())
                        .build())
                .build();
        CreateDataSourceRequest expected = CreateDataSourceRequest
                .builder()
                .type(DataSourceType.S3)
                .configuration(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                        .builder()
                        .s3Configuration(software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder().build())
                        .build())
                .build();
        assertThat(Translator.translateToCreateRequest(resourceModel)).isEqualTo(expected);
    }

    @Test
    void testTranslateSalesforce() {
        ResourceModel resourceModel = ResourceModel
                .builder()
                .type("SALESFORCE")
                .dataSourceConfiguration(DataSourceConfiguration
                        .builder()
                        .salesforceConfiguration(SalesforceConfiguration.builder().build())
                        .build())
                .build();
        CreateDataSourceRequest expected = CreateDataSourceRequest
                .builder()
                .type(DataSourceType.SALESFORCE)
                .configuration(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                        .builder()
                        .salesforceConfiguration(software.amazon.awssdk.services.kendra.model.SalesforceConfiguration.builder().build())
                        .build())
                .build();
        assertThat(Translator.translateToCreateRequest(resourceModel)).isEqualTo(expected);
    }

    @Test
    void testTranslateToSdkDatabase() {
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration
                .builder()
                .build();
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
                .builder()
                .databaseConfiguration(databaseConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expected
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .databaseConfiguration(software.amazon.awssdk.services.kendra.model.DatabaseConfiguration
                        .builder().build())
                .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration, "DATABASE"))
                .isEqualTo(expected);
    }

    @Test
    void testTranslateToModelDatabase() {
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration
                .builder()
                .build();
        DataSourceConfiguration expected = DataSourceConfiguration
                .builder()
                .databaseConfiguration(databaseConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .databaseConfiguration(software.amazon.awssdk.services.kendra.model.DatabaseConfiguration
                        .builder().build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, "DATABASE")).isEqualTo(expected);
    }
}
