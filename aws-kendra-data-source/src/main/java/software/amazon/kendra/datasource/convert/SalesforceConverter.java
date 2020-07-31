package software.amazon.kendra.datasource.convert;

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

import java.util.List;
import java.util.stream.Collectors;

public class SalesforceConverter {

    public static DataSourceConfiguration toSdkDataSourceConfiguration(
            software.amazon.kendra.datasource.SalesforceConfiguration model) {
        return DataSourceConfiguration
                .builder()
                .salesforceConfiguration(toSdk(model))
                .build();
    }

    static SalesforceConfiguration toSdk(software.amazon.kendra.datasource.SalesforceConfiguration model) {
        return SalesforceConfiguration.builder()
                .serverUrl(model.getServerUrl())
                .secretArn(model.getSecretArn())
                .standardObjectConfigurations(toSdkSalesforceStandardObjectConfigurationList(model.getStandardObjectConfigurations()))
                .knowledgeArticleConfiguration(toSdkSalesforceKnowledgeArticleConfiguration(model.getKnowledgeArticleConfiguration()))
                .chatterFeedConfiguration(toSdkSalesforceChatterFeedConfiguration(model.getChatterFeedConfiguration()))
                .crawlAttachments(model.getCrawlAttachments())
                .includeAttachmentFilePatterns(toSdkFilePatterns(model.getIncludeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(toSdkFilePatterns(model.getExcludeAttachmentFilePatterns()))
                .build();
    }

    static SalesforceChatterFeedConfiguration toSdkSalesforceChatterFeedConfiguration(
            software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration model) {
        if (model == null) {
            return null;
        }

        return SalesforceChatterFeedConfiguration
                .builder()
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(toSdkDataSourceToIndexFieldMappingList(model.getFieldMappings()))
                .includeFilterTypes(toSdkSalesforceChatterFeedIncludeFilterType(model.getIncludeFilterTypes()))
                .build();
    }

    static List<SalesforceChatterFeedIncludeFilterType> toSdkSalesforceChatterFeedIncludeFilterType(
            List<String> model) {
        if (model == null) {
            return null;
        }
        return model.stream().map(x -> SalesforceChatterFeedIncludeFilterType.fromValue(x)).collect(Collectors.toList());
    }

    static List<SalesforceStandardObjectConfiguration> toSdkSalesforceStandardObjectConfigurationList(
            List<software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration> model) {
        if (model == null) {
            return null;
        }
        return model.stream().map(x -> toSdkSalesforceStandardObjectConfiguration(x)).collect(Collectors.toList());
    }

    static SalesforceStandardObjectConfiguration toSdkSalesforceStandardObjectConfiguration(
            software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration model) {
        return SalesforceStandardObjectConfiguration.builder()
                .name(model.getName())
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(toSdkDataSourceToIndexFieldMappingList(model.getFieldMappings()))
                .build();
    }


    static SalesforceKnowledgeArticleConfiguration toSdkSalesforceKnowledgeArticleConfiguration(
            software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration model) {
        if (model == null) {
            return null;
        }

        return SalesforceKnowledgeArticleConfiguration
                .builder()
                .includedStates(toSdkSalesforceKnowledgeArticleStateList(model.getIncludedStates()))
                .standardKnowledgeArticleTypeConfiguration(
                        toSdkSalesforceStandardKnowledgeArticleTypeConfiguration(
                                model.getStandardKnowledgeArticleTypeConfiguration()))
                .customKnowledgeArticleTypeConfigurations(
                        toSdkSalesforceCustomKnowledgeArticleTypeConfigurationList(
                                model.getCustomKnowledgeArticleTypeConfigurations()))
                .build();
    }

    static SalesforceStandardKnowledgeArticleTypeConfiguration toSdkSalesforceStandardKnowledgeArticleTypeConfiguration(
            software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration model) {
        if (model == null) {
            return null;
        }
        return SalesforceStandardKnowledgeArticleTypeConfiguration
                .builder()
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(toSdkDataSourceToIndexFieldMappingList(model.getFieldMappings()))
                .build();
    }

    static List<SalesforceCustomKnowledgeArticleTypeConfiguration> toSdkSalesforceCustomKnowledgeArticleTypeConfigurationList(
            List<software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration> model) {
        if (model == null) {
            return null;
        }
        return model.stream().map(x -> toSdkSalesforceCustomKnowledgeArticleTypeConfiguration(x)).collect(Collectors.toList());
    }

    static SalesforceCustomKnowledgeArticleTypeConfiguration toSdkSalesforceCustomKnowledgeArticleTypeConfiguration(
            software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration model) {
        if (model == null) {
            return null;
        }
        return SalesforceCustomKnowledgeArticleTypeConfiguration
                .builder()
                .name(model.getName())
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(toSdkDataSourceToIndexFieldMappingList(model.getFieldMappings()))
                .build();
    }

    static List<SalesforceKnowledgeArticleState> toSdkSalesforceKnowledgeArticleStateList(List<String> modelIncludedStates) {
        if (modelIncludedStates == null) {
            return null;
        }
        return modelIncludedStates.stream().map(x -> SalesforceKnowledgeArticleState.fromValue(x)).collect(Collectors.toList());
    }

    static List<DataSourceToIndexFieldMapping> toSdkDataSourceToIndexFieldMappingList(
            List<software.amazon.kendra.datasource.DataSourceToIndexFieldMapping> modelList) {
        if (modelList == null) {
            return null;
        }
        return modelList.stream().map(x -> toSdkDataSourceToIndexFieldMapping(x)).collect(Collectors.toList());
    }

    static List<String> toSdkFilePatterns(List<String> model) {
        if (model == null) {
            return null;
        }
        return model.stream().collect(Collectors.toList());
    }

    static DataSourceToIndexFieldMapping toSdkDataSourceToIndexFieldMapping(
            software.amazon.kendra.datasource.DataSourceToIndexFieldMapping model) {
        return DataSourceToIndexFieldMapping.builder()
                .indexFieldName(model.getIndexFieldName())
                .dataSourceFieldName(model.getDataSourceFieldName())
                .dateFieldFormat(model.getDateFieldFormat())
                .build();
    }

    public static software.amazon.kendra.datasource.DataSourceConfiguration toModelDataSourceConfiguration(
            SalesforceConfiguration sdk) {
        return software.amazon.kendra.datasource.DataSourceConfiguration
                .builder()
                .salesforceConfiguration(toModel(sdk))
                .build();
    }

    static software.amazon.kendra.datasource.SalesforceConfiguration toModel(SalesforceConfiguration sdk) {
        return software.amazon.kendra.datasource.SalesforceConfiguration
                .builder()
                .serverUrl(sdk.serverUrl())
                .secretArn(sdk.secretArn())
                .crawlAttachments(sdk.crawlAttachments())
                .standardObjectConfigurations(toModelSalesforceStandardObjectConfigurationList(sdk.standardObjectConfigurations()))
                .knowledgeArticleConfiguration(toModelSalesforceKnowledgeArticleConfiguration(sdk.knowledgeArticleConfiguration()))
                .chatterFeedConfiguration(toModelSalesforceChatterFeedConfiguration(sdk.chatterFeedConfiguration()))
                .includeAttachmentFilePatterns(toModelFilePatterns(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(toModelFilePatterns(sdk.excludeAttachmentFilePatterns()))
                .build();
    }

    static software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration
    toModelSalesforceKnowledgeArticleConfiguration(SalesforceKnowledgeArticleConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration
                .builder()
                .standardKnowledgeArticleTypeConfiguration(
                        toModelSalesforceStandardKnowledgeArticleTypeConfiguration(
                                sdk.standardKnowledgeArticleTypeConfiguration()))
                .customKnowledgeArticleTypeConfigurations(
                        toModelSalesforceCustomKnowledgeArticleTypeConfigurationList(
                                sdk.customKnowledgeArticleTypeConfigurations()))
                .includedStates(toModelIncludedStates(sdk.includedStatesAsStrings()))
                .build();
    }

    static List<String> toModelIncludedStates(List<String> sdk) {
        if (sdk == null || sdk.isEmpty()) {
            return null;
        }
        return sdk.stream().collect(Collectors.toList());
    }

    static software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
    toModelSalesforceStandardKnowledgeArticleTypeConfiguration(
            SalesforceStandardKnowledgeArticleTypeConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return software.amazon.kendra.datasource.SalesforceStandardKnowledgeArticleTypeConfiguration
                .builder()
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .fieldMappings(toModelDataSourceToIndexFieldMappingList(sdk.fieldMappings()))
                .build();
    }

    static List<software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration>
    toModelSalesforceCustomKnowledgeArticleTypeConfigurationList(
            List<SalesforceCustomKnowledgeArticleTypeConfiguration> sdk) {
        if (sdk == null || sdk.isEmpty()) {
            return null;
        }
        return sdk.stream().map(x -> toModelSalesforceCustomKnowledgeArticleTypeConfiguration(x))
                .collect(Collectors.toList());
    }

    static software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
    toModelSalesforceCustomKnowledgeArticleTypeConfiguration(
            SalesforceCustomKnowledgeArticleTypeConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return software.amazon.kendra.datasource.SalesforceCustomKnowledgeArticleTypeConfiguration
                .builder()
                .name(sdk.name())
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .fieldMappings(toModelDataSourceToIndexFieldMappingList(sdk.fieldMappings()))
                .build();

    }

    static List<software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration>
    toModelSalesforceStandardObjectConfigurationList(List<SalesforceStandardObjectConfiguration> sdk) {
        if (sdk == null || sdk.isEmpty()) {
            return null;
        }
        return sdk.stream().map(x -> toModelSalesforceStandardObjectConfiguration(x)).collect(Collectors.toList());
    }

    static software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration toModelSalesforceStandardObjectConfiguration(
            SalesforceStandardObjectConfiguration sdk) {
        if (sdk == null) {
            return null;
        }

        return software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration
                .builder()
                .name(sdk.nameAsString())
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .fieldMappings(toModelDataSourceToIndexFieldMappingList(sdk.fieldMappings()))
                .build();
    }

    static List<String> toModelFilePatterns(List<String> model) {
        if (model == null || model.isEmpty()) {
            return null;
        }
        return model.stream().collect(Collectors.toList());
    }

    static List<software.amazon.kendra.datasource.DataSourceToIndexFieldMapping> toModelDataSourceToIndexFieldMappingList(
            List<DataSourceToIndexFieldMapping> sdk) {
        if (sdk == null) {
            return null;
        }
        return sdk.stream().map(x -> toModelDataSourceToIndexFieldMapping(x)).collect(Collectors.toList());
    }

    static software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration toModelSalesforceChatterFeedConfiguration(
            SalesforceChatterFeedConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return software.amazon.kendra.datasource.SalesforceChatterFeedConfiguration
                .builder()
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .fieldMappings(toModelDataSourceToIndexFieldMappingList(sdk.fieldMappings()))
                .includeFilterTypes(toModelIncludeFilterTypes(sdk.includeFilterTypes()))
                .build();
    }

    static List<String> toModelIncludeFilterTypes(List<SalesforceChatterFeedIncludeFilterType> sdk) {
        if (sdk == null || sdk.isEmpty()) {
            return null;
        }
        return sdk.stream().map(x -> x.toString()).collect(Collectors.toList());
    }

    static software.amazon.kendra.datasource.DataSourceToIndexFieldMapping toModelDataSourceToIndexFieldMapping(
            DataSourceToIndexFieldMapping sdk) {
        return software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                .builder()
                .dataSourceFieldName(sdk.dataSourceFieldName())
                .dateFieldFormat(sdk.dateFieldFormat())
                .indexFieldName(sdk.indexFieldName())
                .build();
    }

}
