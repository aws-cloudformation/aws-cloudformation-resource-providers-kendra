package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.kendra.model.ServiceNowAuthenticationType;
import software.amazon.kendra.datasource.DataSourceToIndexFieldMapping;
import software.amazon.kendra.datasource.ServiceNowConfiguration;
import software.amazon.kendra.datasource.ServiceNowKnowledgeArticleConfiguration;
import software.amazon.kendra.datasource.ServiceNowServiceCatalogConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceNowConverterTest {

    @Test
    void testToSdkStringsTopLevel() {
        String hostUrl = "hostUrl";
        String secretArn = "secretArn";
        String version = "version";
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .hostUrl(hostUrl)
                .secretArn(secretArn)
                .serviceNowBuildVersion(version)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .hostUrl(hostUrl)
                        .secretArn(secretArn)
                        .serviceNowBuildVersion(version)
                        .build();

        assertThat(ServiceNowConverter.toSdkDataSourceConfiguration(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkKnowledgeArticle() {
        String documentDataFieldName = "documentDataFieldName";
        String documentTitleFieldName = "documentTitleFieldName";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dateFieldFormat";
        DataSourceToIndexFieldMapping dataSourceToIndexFieldMapping =
                DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        List<String> includes = Arrays.asList("a");
        List<String> excludes = Arrays.asList("b");
        ServiceNowKnowledgeArticleConfiguration modelKA = ServiceNowKnowledgeArticleConfiguration
                .builder()
                .documentTitleFieldName(documentTitleFieldName)
                .documentDataFieldName(documentDataFieldName)
                .crawlAttachments(true)
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .includeAttachmentFilePatterns(includes)
                .excludeAttachmentFilePatterns(excludes)
                .build();
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .knowledgeArticleConfiguration(modelKA)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .knowledgeArticleConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration
                                .builder()
                                .crawlAttachments(true)
                                .documentDataFieldName(documentDataFieldName)
                                .documentTitleFieldName(documentTitleFieldName)
                                .includeAttachmentFilePatterns(includes)
                                .excludeAttachmentFilePatterns(excludes)
                                .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping
                                        .builder()
                                        .dataSourceFieldName(dataSourceFieldName)
                                        .dateFieldFormat(dateFieldFormat)
                                        .indexFieldName(indexFieldName)
                                        .build()))
                                .build())
                        .build();

        assertThat(ServiceNowConverter.toSdkDataSourceConfiguration(model)).isEqualTo(sdk);
    }

    @Test
    void testToModelStringsTopLevel() {
        String hostUrl = "hostUrl";
        String secretArn = "secretArn";
        String version = "version";
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .hostUrl(hostUrl)
                .secretArn(secretArn)
                .serviceNowBuildVersion(version)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .hostUrl(hostUrl)
                        .secretArn(secretArn)
                        .serviceNowBuildVersion(version)
                        .build();

        assertThat(ServiceNowConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelKnowledgeArticle() {
        String documentDataFieldName = "documentDataFieldName";
        String documentTitleFieldName = "documentTitleFieldName";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dateFieldFormat";
        DataSourceToIndexFieldMapping dataSourceToIndexFieldMapping =
                DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        List<String> includes = Arrays.asList("a");
        List<String> excludes = Arrays.asList("b");
        ServiceNowKnowledgeArticleConfiguration modelKA = ServiceNowKnowledgeArticleConfiguration
                .builder()
                .documentTitleFieldName(documentTitleFieldName)
                .documentDataFieldName(documentDataFieldName)
                .crawlAttachments(true)
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .includeAttachmentFilePatterns(includes)
                .excludeAttachmentFilePatterns(excludes)
                .build();
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .knowledgeArticleConfiguration(modelKA)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .knowledgeArticleConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration
                                .builder()
                                .crawlAttachments(true)
                                .documentDataFieldName(documentDataFieldName)
                                .documentTitleFieldName(documentTitleFieldName)
                                .includeAttachmentFilePatterns(includes)
                                .excludeAttachmentFilePatterns(excludes)
                                .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping
                                        .builder()
                                        .dataSourceFieldName(dataSourceFieldName)
                                        .dateFieldFormat(dateFieldFormat)
                                        .indexFieldName(indexFieldName)
                                        .build()))
                                .build())
                        .build();

        assertThat(ServiceNowConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelCatalog() {
        String documentDataFieldName = "documentDataFieldName";
        String documentTitleFieldName = "documentTitleFieldName";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dateFieldFormat";
        DataSourceToIndexFieldMapping dataSourceToIndexFieldMapping =
                DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        List<String> includes = Arrays.asList("a");
        List<String> excludes = Arrays.asList("b");
        ServiceNowServiceCatalogConfiguration modelCatalogConfig = ServiceNowServiceCatalogConfiguration
                .builder()
                .crawlAttachments(true)
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .includeAttachmentFilePatterns(includes)
                .excludeAttachmentFilePatterns(excludes)
                .build();
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .serviceCatalogConfiguration(modelCatalogConfig)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .serviceCatalogConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowServiceCatalogConfiguration
                                .builder()
                                .crawlAttachments(true)
                                .documentDataFieldName(documentDataFieldName)
                                .documentTitleFieldName(documentTitleFieldName)
                                .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping
                                        .builder()
                                        .dataSourceFieldName(dataSourceFieldName)
                                        .dateFieldFormat(dateFieldFormat)
                                        .indexFieldName(indexFieldName)
                                        .build()))
                                .includeAttachmentFilePatterns(includes)
                                .excludeAttachmentFilePatterns(excludes)
                                .build())
                        .build();

        assertThat(ServiceNowConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    public void testServiceNowEnhancements() {

        List<String> authenticationTypes = Arrays.asList(
            ServiceNowAuthenticationType.HTTP_BASIC.toString(), ServiceNowAuthenticationType.OAUTH2.toString(), null);

        List<String> filterQueries = Arrays.asList("hello world", "", null);

        for (String authType : authenticationTypes) {
            for (String filterQuery : filterQueries) {

                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdkConfig =
                    software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration.builder()
                        .authenticationType(authType)
                        .knowledgeArticleConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration.builder()
                            .filterQuery(filterQuery)
                            .build())
                        .build();

                ServiceNowConfiguration modelConfig = ServiceNowConfiguration.builder()
                        .authenticationType(authType)
                        .knowledgeArticleConfiguration(ServiceNowKnowledgeArticleConfiguration.builder()
                            .filterQuery(filterQuery)
                            .build())
                        .build();

                assertThat(ServiceNowConverter.toModel(sdkConfig))
                    .as("Sdk -> Model")
                    .isEqualTo(modelConfig);

                assertThat(ServiceNowConverter.toSdkDataSourceConfiguration(modelConfig))
                    .as("Model -> Sdk")
                    .isEqualTo(sdkConfig);

                assertThat(ServiceNowConverter.toSdkDataSourceConfiguration(ServiceNowConverter.toModel(sdkConfig)))
                    .as("Sdk -> Model -> Sdk")
                    .isEqualTo(sdkConfig);

                assertThat(ServiceNowConverter.toModel(ServiceNowConverter.toSdkDataSourceConfiguration(modelConfig)))
                    .as("Model -> Sdk -> Model")
                    .isEqualTo(modelConfig);
            }
        }
    }

    @Test
    public void testEmptySdkListsBecomeNullsInModel() {
        assertThat(
            ServiceNowConverter.toModel(
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration.builder()
                    .knowledgeArticleConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration.builder()
                        .fieldMappings(Collections.emptyList())
                        .includeAttachmentFilePatterns(Collections.emptyList())
                        .excludeAttachmentFilePatterns(Collections.emptyList())
                        .build())
                    .serviceCatalogConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowServiceCatalogConfiguration.builder()
                        .fieldMappings(Collections.emptyList())
                        .includeAttachmentFilePatterns(Collections.emptyList())
                        .excludeAttachmentFilePatterns(Collections.emptyList())
                        .build())
                    .build()
            )
        ).isEqualTo(
            ServiceNowConfiguration.builder()
                .knowledgeArticleConfiguration(ServiceNowKnowledgeArticleConfiguration.builder()
                    .fieldMappings(null)
                    .includeAttachmentFilePatterns(null)
                    .excludeAttachmentFilePatterns(null)
                    .build())
                .serviceCatalogConfiguration(ServiceNowServiceCatalogConfiguration.builder()
                    .fieldMappings(null)
                    .includeAttachmentFilePatterns(null)
                    .excludeAttachmentFilePatterns(null)
                    .build())
                .build()
        );
    }
}
