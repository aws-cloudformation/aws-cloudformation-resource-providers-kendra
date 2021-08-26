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
    void testTranslateToCreateRequest_WithConfluenceDataSource() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .type(DataSourceType.CONFLUENCE.toString())
            .dataSourceConfiguration(DataSourceConfiguration.builder()
                .confluenceConfiguration(ConfluenceConfiguration.builder().build())
                .build())
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.configuration().confluenceConfiguration()).isNotNull();
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
        assertThat(createDataSourceRequest.configuration()).isNull();
    }

    @Test
    void testTranslateToCreateRequest_WithCustomDataSource() {
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .indexId(indexId)
            .type("CUSTOM")
            .description("description")
            .build();
        CreateDataSourceRequest createDataSourceRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(createDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(createDataSourceRequest.configuration()).isNull();
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
        assertThat(updateDataSourceRequest.name()).isEqualTo(null);
        assertThat(updateDataSourceRequest.roleArn()).isEqualTo(null);
        assertThat(updateDataSourceRequest.schedule()).isEqualTo(null);
        assertThat(updateDataSourceRequest.configuration())
                .isEqualTo(null);
    }

    @Test
    void testTranslateToUpdateRequest_DescriptionNotProvided() {
        String id = "id";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
            .builder()
            .id(id)
            .indexId(indexId)
            .build();
        UpdateDataSourceRequest updateDataSourceRequest = Translator.translateToUpdateRequest(resourceModel);
        assertThat(updateDataSourceRequest.id()).isEqualTo(id);
        assertThat(updateDataSourceRequest.indexId()).isEqualTo(indexId);
        assertThat(updateDataSourceRequest.description()).isEqualTo("");
        assertThat(updateDataSourceRequest.name()).isEqualTo(null);
        assertThat(updateDataSourceRequest.roleArn()).isEqualTo(null);
        assertThat(updateDataSourceRequest.schedule()).isEqualTo(null);
        assertThat(updateDataSourceRequest.configuration())
            .isEqualTo(null);
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

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration));
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

    @Test
    void testTranslateToModelServiceNow() {
        ServiceNowConfiguration serviceNowConfiguration = ServiceNowConfiguration
                .builder()
                .build();
        DataSourceConfiguration expected = DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(serviceNowConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, "SERVICENOW")).isEqualTo(expected);
    }

    @Test
    void testTranslateToSdkServiceNow() {
        ServiceNowConfiguration serviceNowConfiguration = ServiceNowConfiguration
                .builder()
                .build();
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(serviceNowConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expected
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .build())
                .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
                .isEqualTo(expected);
    }

    @Test
    void testTranslateToModelOneDrive() {
        OneDriveConfiguration oneDriveConfiguration = OneDriveConfiguration
                .builder()
                .build();
        DataSourceConfiguration expected = DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(oneDriveConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(software.amazon.awssdk.services.kendra.model.OneDriveConfiguration
                        .builder()
                        .build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, "ONEDRIVE")).isEqualTo(expected);
    }

    @Test
    void testTranslateToSdkOneDrive() {
        OneDriveConfiguration oneDriveConfiguration = OneDriveConfiguration
                .builder()
                .build();
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(oneDriveConfiguration)
                .build();

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expected
                = software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(software.amazon.awssdk.services.kendra.model.OneDriveConfiguration
                        .builder()
                        .build())
                .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
                .isEqualTo(expected);
    }

    @Test
    void testTranslateToModelConfluence() {

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
            .confluenceConfiguration(software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration.builder().build())
            .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, DataSourceType.CONFLUENCE.toString()))
            .isEqualTo(DataSourceConfiguration.builder()
                .confluenceConfiguration(ConfluenceConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToSdkConfluence() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
            .builder()
            .confluenceConfiguration(ConfluenceConfiguration.builder().build())
            .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
            .isEqualTo(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .confluenceConfiguration(software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToModelGoogleDrive() {

        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .googleDriveConfiguration(software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration.builder().build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, DataSourceType.GOOGLEDRIVE.toString()))
            .isEqualTo(DataSourceConfiguration.builder()
                .googleDriveConfiguration(GoogleDriveConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToSdkGoogleDrive() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
            .builder()
            .googleDriveConfiguration(GoogleDriveConfiguration.builder().build())
            .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
            .isEqualTo(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .googleDriveConfiguration(software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToModelWebCrawler() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .webCrawlerConfiguration(software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration.builder().build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, DataSourceType.WEBCRAWLER.toString()))
            .isEqualTo(DataSourceConfiguration.builder()
                .webCrawlerConfiguration(WebCrawlerConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToSdkWebCrawler() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
            .builder()
            .webCrawlerConfiguration(WebCrawlerConfiguration.builder().build())
            .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
            .isEqualTo(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .webCrawlerConfiguration(software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration.builder().build())
                .build());
        }

    @Test
    void testTranslateToModelWorkDocs() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder().build())
                .build();

        assertThat(Translator.toModelDataSourceConfiguration(dataSourceConfiguration, DataSourceType.WORKDOCS.toString()))
            .isEqualTo(DataSourceConfiguration.builder()
                .workDocsConfiguration(WorkDocsConfiguration.builder().build())
                .build());
    }

    @Test
    void testTranslateToSdkWorkDocs() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration
            .builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder().build())
            .build();

        assertThat(Translator.toSdkDataSourceConfiguration(dataSourceConfiguration))
            .isEqualTo(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder().build())
                .build());
    }

    @Test
    void translateToSdkConfiguration_WHEN_Configuration_Null(){
        assertThat(Translator.toSdkDataSourceConfiguration(null))
            .isEqualTo(null);
    }

    @Test
    void testTranslateS3_WHEN_Configuration_NULL() {
        ResourceModel resourceModel = ResourceModel
            .builder()
            .type("S3")
            .dataSourceConfiguration(null)
            .build();
        CreateDataSourceRequest expected = CreateDataSourceRequest
            .builder()
            .type(DataSourceType.S3)
            .build();
        assertThat(Translator.translateToCreateRequest(resourceModel)).isEqualTo(expected);
    }
}
