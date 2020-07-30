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

    public static DataSourceConfiguration sdkDataSourceConfiguration(
            software.amazon.kendra.datasource.SalesforceConfiguration model) {
        return DataSourceConfiguration
                .builder()
                .salesforceConfiguration(toSdk(model))
                .build();
    }

    static SalesforceConfiguration toSdk(software.amazon.kendra.datasource.SalesforceConfiguration model) {
        SalesforceConfiguration.Builder builder = SalesforceConfiguration.builder();
        builder.serverUrl(model.getServerUrl());
        builder.secretArn(model.getSecretArn());
        builder.standardObjectConfigurations(toSdkSalesforceStandardObjectConfigurationList(model.getStandardObjectConfigurations()));
        builder.knowledgeArticleConfiguration(toSdkSalesforceKnowledgeArticleConfiguration(model.getKnowledgeArticleConfiguration()));
        builder.chatterFeedConfiguration(toSdkSalesforceChatterFeedConfiguration(model.getChatterFeedConfiguration()));
        builder.crawlAttachments(model.getCrawlAttachments());
        builder.includeAttachmentFilePatterns(toFilePatterns(model.getIncludeAttachmentFilePatterns()));
        builder.excludeAttachmentFilePatterns(toFilePatterns(model.getExcludeAttachmentFilePatterns()));
        return builder.build();
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
        SalesforceStandardObjectConfiguration.Builder builder = SalesforceStandardObjectConfiguration.builder();
        builder.name(model.getName());
        builder.documentDataFieldName(model.getDocumentDataFieldName());
        builder.documentTitleFieldName(model.getDocumentTitleFieldName());
        builder.fieldMappings(toSdkDataSourceToIndexFieldMappingList(model.getFieldMappings()));
        return builder.build();
    }


    static SalesforceKnowledgeArticleConfiguration toSdkSalesforceKnowledgeArticleConfiguration(
            software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration model) {
        if (model == null) {
            return null;
        }

        SalesforceKnowledgeArticleConfiguration.Builder builder = SalesforceKnowledgeArticleConfiguration
                .builder();
        builder.includedStates(toSdkSalesforceKnowledgeArticleStateList(model.getIncludedStates()));
        builder.standardKnowledgeArticleTypeConfiguration(
                toSdkSalesforceStandardKnowledgeArticleTypeConfiguration(
                        model.getStandardKnowledgeArticleTypeConfiguration()));
        builder.customKnowledgeArticleTypeConfigurations(
                toSdkSalesforceCustomKnowledgeArticleTypeConfigurationList(model.getCustomKnowledgeArticleTypeConfigurations()));
        return builder.build();
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

    static List<String> toFilePatterns(List<String> model) {
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

    public static software.amazon.kendra.datasource.DataSourceConfiguration modelDataSourceConfiguration(
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
                .includeAttachmentFilePatterns(toFilePatterns(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(toFilePatterns(sdk.excludeAttachmentFilePatterns()))
                .build();
    }



}
