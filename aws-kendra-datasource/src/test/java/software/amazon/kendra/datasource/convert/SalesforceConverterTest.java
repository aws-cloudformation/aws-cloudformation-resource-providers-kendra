package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.DataSourceConfiguration;
import software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.SalesforceChatterFeedConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceChatterFeedIncludeFilterType;
import software.amazon.awssdk.services.kendra.model.SalesforceConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceCustomKnowledgeArticleTypeConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceKnowledgeArticleConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceKnowledgeArticleState;
import software.amazon.awssdk.services.kendra.model.SalesforceStandardKnowledgeArticleTypeConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceStandardObjectConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SalesforceConverterTest {

    // Tests to SDK from model
    @Test
    void sdkDataSourceConfiguration() {
        SalesforceConfiguration expected = SalesforceConfiguration.builder().build();
        software.amazon.kendra.datasource.SalesforceConfiguration input = software.amazon.kendra.datasource.SalesforceConfiguration
                .builder().build();
        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkServerUrlAndSecretArnAndCrawlAttachments() {
        String serverUrl = "serverUrl";
        String secretArn = "secretArn";
        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .serverUrl(serverUrl)
                .secretArn(secretArn)
                .crawlAttachments(true)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .serverUrl(serverUrl)
                        .secretArn(secretArn)
                        .crawlAttachments(true)
                        .build();

        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkSalesforceStandardObjectConfigurationList() {
        String name = "name";
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
        List<DataSourceToIndexFieldMapping> fieldMappingArrayList = Arrays.asList(dataSourceToIndexFieldMapping);
        SalesforceStandardObjectConfiguration salesforceStandardObjectConfiguration =
                SalesforceStandardObjectConfiguration.builder()
                        .name(name)
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(fieldMappingArrayList)
                        .build();
        List<SalesforceStandardObjectConfiguration> salesforceStandardObjectConfigurationList =
                Arrays.asList(salesforceStandardObjectConfiguration);

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration modelSalesforceStandardObjectConfiguration
                = software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration
                .builder()
                .name(name)
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                .build();

        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .standardObjectConfigurations(salesforceStandardObjectConfigurationList)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .standardObjectConfigurations(Arrays.asList(modelSalesforceStandardObjectConfiguration))
                        .build();

        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkNullFieldMappings() {
        software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration modelSalesforceStandardObjectConfiguration
                = software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration
                .builder()
                .name("name")
                .build();
        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .standardObjectConfigurations(Arrays.asList(modelSalesforceStandardObjectConfiguration))
                        .build();

        SalesforceConfiguration actual = SalesforceConverter.toSdk(input);
        assertThat(actual.standardObjectConfigurations().size()).isEqualTo(1);
        assertThat(actual.standardObjectConfigurations().get(0).fieldMappings()).isEmpty();
    }

    @Test
    void testKnowledgeArticleConfiguration() {
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
        SalesforceStandardKnowledgeArticleTypeConfiguration salesforceStandardKnowledgeArticleTypeConfiguration =
                SalesforceStandardKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                        .build();
        String name = "name";
        SalesforceCustomKnowledgeArticleTypeConfiguration customKnowledgeArticleTypeConfiguration =
                SalesforceCustomKnowledgeArticleTypeConfiguration
                        .builder()
                        .name(name)
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                        .build();

        SalesforceKnowledgeArticleConfiguration salesforceKnowledgeArticleConfiguration = SalesforceKnowledgeArticleConfiguration
                .builder()
                .includedStates(SalesforceKnowledgeArticleState.ARCHIVED)
                .standardKnowledgeArticleTypeConfiguration(salesforceStandardKnowledgeArticleTypeConfiguration)
                .customKnowledgeArticleTypeConfigurations(customKnowledgeArticleTypeConfiguration)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
                modelSalesforceStandardKnowledgeArticleTypeConfiguration =
                software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                        .build();
        software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
                modelSalesforceCustomKnowledgeArticleTypeConfiguration =
                software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentTitleFieldName(documentTitleFieldName)
                        .documentDataFieldName(documentDataFieldName)
                        .name(name)
                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                        .build();
        software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration modelSalesforceKnowledgeArticleConfiguration =
                software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration
                        .builder()
                        .includedStates(Arrays.asList("ARCHIVED"))
                        .standardKnowledgeArticleTypeConfiguration(modelSalesforceStandardKnowledgeArticleTypeConfiguration)
                        .customKnowledgeArticleTypeConfigurations(Arrays.asList(modelSalesforceCustomKnowledgeArticleTypeConfiguration))
                        .build();

        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .knowledgeArticleConfiguration(salesforceKnowledgeArticleConfiguration)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .knowledgeArticleConfiguration(modelSalesforceKnowledgeArticleConfiguration)
                        .build();

        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testChatterFeedConfiguration() {
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
        SalesforceChatterFeedConfiguration salesforceChatterFeedConfiguration = SalesforceChatterFeedConfiguration
                .builder()
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .includeFilterTypes(SalesforceChatterFeedIncludeFilterType.ACTIVE_USER)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration modelSalesforceChatterFeedConfiguration =
                software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration
                .builder()
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                .includeFilterTypes(Arrays.asList("ACTIVE_USER"))
                .build();

        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .chatterFeedConfiguration(salesforceChatterFeedConfiguration)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .chatterFeedConfiguration(modelSalesforceChatterFeedConfiguration)
                        .build();

        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkFilePatterns() {
        List<String> include = Arrays.asList("txt");
        List<String> exclude = Arrays.asList("txt");
        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .includeAttachmentFilePatterns(include)
                .excludeAttachmentFilePatterns(exclude)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .includeAttachmentFilePatterns(include)
                        .excludeAttachmentFilePatterns(exclude)
                        .build();

        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkSalesforceStandardKnowledgeArticleTypeConfiguration() {
        assertThat(SalesforceConverter.toSdkSalesforceStandardKnowledgeArticleTypeConfiguration(null)).isNull();
    }

    @Test
    void testToSdkSalesforceCustomKnowledgeArticleTypeConfiguration() {
        assertThat(SalesforceConverter.toSdkSalesforceCustomKnowledgeArticleTypeConfiguration(null)).isNull();
    }

    // Tests to model from SDK
    @Test
    void testModelDataSourceConfiguration() {
        assertThat(SalesforceConverter.toModelDataSourceConfiguration(SalesforceConfiguration.builder().build())).isEqualTo(
                software.amazon.kendra.datasource.DataSourceConfiguration
                        .builder()
                        .salesforceConfiguration(
                                software.amazon.kendra.datasource.SalesforceConfiguration.builder().build()).build());
    }

    @Test
    void testToModelServerUrlAndSecretArnAndCrawlAttachments() {
        String serverUrl = "serverUrl";
        String secretArn = "secretArn";

        software.amazon.kendra.datasource.SalesforceConfiguration expected =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .serverUrl(serverUrl)
                        .secretArn(secretArn)
                        .crawlAttachments(true)
                        .build();

        SalesforceConfiguration input = SalesforceConfiguration
                .builder()
                .serverUrl(serverUrl)
                .secretArn(secretArn)
                .crawlAttachments(true)
                .build();

        assertThat(SalesforceConverter.toModel(input)).isEqualTo(expected);
    }

    @Test
    void testToModelFilePatterns() {
        List<String> include = Arrays.asList("txt");
        List<String> exclude = Arrays.asList("txt");


        software.amazon.kendra.datasource.SalesforceConfiguration expected =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .includeAttachmentFilePatterns(include)
                        .excludeAttachmentFilePatterns(exclude)
                        .build();

        SalesforceConfiguration input = SalesforceConfiguration
                .builder()
                .includeAttachmentFilePatterns(include)
                .excludeAttachmentFilePatterns(exclude)
                .build();

        assertThat(SalesforceConverter.toModel(input)).isEqualTo(expected);
    }

    @Test
    void testStandardObjectConfigurations() {
        String name = "name";
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

        List<DataSourceToIndexFieldMapping> fieldMappingArrayList = Arrays.asList(dataSourceToIndexFieldMapping);
        SalesforceStandardObjectConfiguration salesforceStandardObjectConfiguration =
                SalesforceStandardObjectConfiguration.builder()
                        .name(name)
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(fieldMappingArrayList)
                        .build();
        List<SalesforceStandardObjectConfiguration> salesforceStandardObjectConfigurationList =
                Arrays.asList(salesforceStandardObjectConfiguration);

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration modelSalesforceStandardObjectConfiguration
                = software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration
                .builder()
                .name(name)
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                .build();

        SalesforceConfiguration input = SalesforceConfiguration
                .builder()
                .standardObjectConfigurations(salesforceStandardObjectConfigurationList)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration expected =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .standardObjectConfigurations(Arrays.asList(modelSalesforceStandardObjectConfiguration))
                        .build();

        assertThat(SalesforceConverter.toModel(input)).isEqualTo(expected);
    }

    @Test
    void testToModelKnowledgeArticleConfiguration() {
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
        SalesforceStandardKnowledgeArticleTypeConfiguration salesforceStandardKnowledgeArticleTypeConfiguration =
                SalesforceStandardKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                        .build();
        String name = "name";
        SalesforceCustomKnowledgeArticleTypeConfiguration customKnowledgeArticleTypeConfiguration =
                SalesforceCustomKnowledgeArticleTypeConfiguration
                        .builder()
                        .name(name)
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                        .build();

        SalesforceKnowledgeArticleConfiguration salesforceKnowledgeArticleConfiguration = SalesforceKnowledgeArticleConfiguration
                .builder()
                .includedStates(SalesforceKnowledgeArticleState.ARCHIVED)
                .standardKnowledgeArticleTypeConfiguration(salesforceStandardKnowledgeArticleTypeConfiguration)
                .customKnowledgeArticleTypeConfigurations(customKnowledgeArticleTypeConfiguration)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
                modelSalesforceStandardKnowledgeArticleTypeConfiguration =
                software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                        .build();
        software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
                modelSalesforceCustomKnowledgeArticleTypeConfiguration =
                software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
                        .builder()
                        .documentTitleFieldName(documentTitleFieldName)
                        .documentDataFieldName(documentDataFieldName)
                        .name(name)
                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                        .build();
        software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration modelSalesforceKnowledgeArticleConfiguration =
                software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration
                        .builder()
                        .includedStates(Arrays.asList("ARCHIVED"))
                        .standardKnowledgeArticleTypeConfiguration(modelSalesforceStandardKnowledgeArticleTypeConfiguration)
                        .customKnowledgeArticleTypeConfigurations(Arrays.asList(modelSalesforceCustomKnowledgeArticleTypeConfiguration))
                        .build();

        SalesforceConfiguration input = SalesforceConfiguration
                .builder()
                .knowledgeArticleConfiguration(salesforceKnowledgeArticleConfiguration)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration expected =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .knowledgeArticleConfiguration(modelSalesforceKnowledgeArticleConfiguration)
                        .build();

        assertThat(SalesforceConverter.toModel(input)).isEqualTo(expected);
    }

    @Test
    void testToModelChatterFeedConfiguration() {
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
        SalesforceChatterFeedConfiguration salesforceChatterFeedConfiguration = SalesforceChatterFeedConfiguration
                .builder()
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .documentDataFieldName(documentDataFieldName)
                .documentTitleFieldName(documentTitleFieldName)
                .includeFilterTypes(SalesforceChatterFeedIncludeFilterType.ACTIVE_USER)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration modelSalesforceChatterFeedConfiguration =
                software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration
                        .builder()
                        .documentDataFieldName(documentDataFieldName)
                        .documentTitleFieldName(documentTitleFieldName)
                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                        .includeFilterTypes(Arrays.asList("ACTIVE_USER"))
                        .build();

        SalesforceConfiguration input = SalesforceConfiguration
                .builder()
                .chatterFeedConfiguration(salesforceChatterFeedConfiguration)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration expected =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .chatterFeedConfiguration(modelSalesforceChatterFeedConfiguration)
                        .build();

        assertThat(SalesforceConverter.toModel(input)).isEqualTo(expected);
    }

    @Test
    void testToModelSalesforceStandardKnowledgeArticleTypeConfiguration() {
        assertThat(SalesforceConverter.toModelSalesforceStandardKnowledgeArticleTypeConfiguration(null)).isNull();
    }

    @Test
    void testToModelSalesforceStandardObjectConfiguration() {
        assertThat(SalesforceConverter.toModelSalesforceStandardObjectConfiguration(null)).isNull();
    }

}
