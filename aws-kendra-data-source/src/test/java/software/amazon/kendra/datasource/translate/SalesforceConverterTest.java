package software.amazon.kendra.datasource.translate;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.SalesforceChatterFeedConfiguration;
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

    @Test
    void testToSdkServerUrlAndSecretArn() {
        String serverUrl = "serverUrl";
        String secretArn = "secretArn";
        SalesforceConfiguration expected = SalesforceConfiguration
                .builder()
                .serverUrl(serverUrl)
                .secretArn(secretArn)
                .build();

        software.amazon.kendra.datasource.SalesforceConfiguration input =
                software.amazon.kendra.datasource.SalesforceConfiguration
                        .builder()
                        .serverUrl(serverUrl)
                        .secretArn(secretArn)
                        .build();
        assertThat(SalesforceConverter.toSdk(input)).isEqualTo(expected);
    }

    @Test
    void testToSdkSalesforceSotandardObjectConfigurationList() {
        String name = "name";
        String documentDataFieldName = "documentDataFieldName";
        String documentTitleFieldName = "documentTitleFieldName";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dataSourceFieldName";
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
    void testKnowledgeArticleConfiguration() {
        String documentDataFieldName = "documentDataFieldName";
        String documentTitleFieldName = "documentTitleFieldName";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dataSourceFieldName";
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
        SalesforceChatterFeedConfiguration salesforceChatterFeedConfiguration = SalesforceChatterFeedConfiguration
                .builder()
                .
    }

}
