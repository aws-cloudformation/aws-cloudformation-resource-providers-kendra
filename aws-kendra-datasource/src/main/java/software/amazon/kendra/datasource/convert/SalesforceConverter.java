package software.amazon.kendra.datasource.convert;

import software.amazon.awssdk.services.kendra.model.SalesforceChatterFeedConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceChatterFeedIncludeFilterType;
import software.amazon.awssdk.services.kendra.model.SalesforceConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceCustomKnowledgeArticleTypeConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceKnowledgeArticleConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceKnowledgeArticleState;
import software.amazon.awssdk.services.kendra.model.SalesforceStandardKnowledgeArticleTypeConfiguration;
import software.amazon.awssdk.services.kendra.model.SalesforceStandardObjectConfiguration;

public class SalesforceConverter {

    public static SalesforceConfiguration toSdk(software.amazon.kendra.datasource.SalesforceConfiguration model) {
        if (model == null) {
            return null;
        }
        return SalesforceConfiguration.builder()
                .serverUrl(model.getServerUrl())
                .secretArn(model.getSecretArn())
                .standardObjectConfigurations(ListConverter.toSdk(model.getStandardObjectConfigurations(), SalesforceConverter::toSdkSalesforceStandardObjectConfiguration))
                .knowledgeArticleConfiguration(toSdkSalesforceKnowledgeArticleConfiguration(model.getKnowledgeArticleConfiguration()))
                .chatterFeedConfiguration(toSdkSalesforceChatterFeedConfiguration(model.getChatterFeedConfiguration()))
                .crawlAttachments(model.getCrawlAttachments())
                .includeAttachmentFilePatterns(StringListConverter.toSdk(model.getIncludeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toSdk(model.getExcludeAttachmentFilePatterns()))
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
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .includeFilterTypes(ListConverter.toSdk(model.getIncludeFilterTypes(), SalesforceChatterFeedIncludeFilterType::fromValue))
                .build();
    }

    static SalesforceStandardObjectConfiguration toSdkSalesforceStandardObjectConfiguration(
            software.amazon.kendra.datasource.SalesforceStandardObjectConfiguration model) {
        return SalesforceStandardObjectConfiguration.builder()
                .name(model.getName())
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .build();
    }


    static SalesforceKnowledgeArticleConfiguration toSdkSalesforceKnowledgeArticleConfiguration(
            software.amazon.kendra.datasource.SalesforceKnowledgeArticleConfiguration model) {
        if (model == null) {
            return null;
        }
        return SalesforceKnowledgeArticleConfiguration
                .builder()
                .includedStates(ListConverter.toSdk(model.getIncludedStates(), SalesforceKnowledgeArticleState::fromValue))
                .standardKnowledgeArticleTypeConfiguration(
                        toSdkSalesforceStandardKnowledgeArticleTypeConfiguration(
                                model.getStandardKnowledgeArticleTypeConfiguration()))
                .customKnowledgeArticleTypeConfigurations(
                        ListConverter.toSdk(model.getCustomKnowledgeArticleTypeConfigurations(),
                                SalesforceConverter::toSdkSalesforceCustomKnowledgeArticleTypeConfiguration
                        ))
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
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .build();
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
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
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
        if (sdk == null) {
            return null;
        }
        return software.amazon.kendra.datasource.SalesforceConfiguration
                .builder()
                .serverUrl(sdk.serverUrl())
                .secretArn(sdk.secretArn())
                .crawlAttachments(sdk.crawlAttachments())
                .standardObjectConfigurations(ListConverter.toModel(
                        sdk.standardObjectConfigurations(),
                        SalesforceConverter::toModelSalesforceStandardObjectConfiguration))
                .knowledgeArticleConfiguration(toModelSalesforceKnowledgeArticleConfiguration(sdk.knowledgeArticleConfiguration()))
                .chatterFeedConfiguration(toModelSalesforceChatterFeedConfiguration(sdk.chatterFeedConfiguration()))
                .includeAttachmentFilePatterns(StringListConverter.toModel(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toModel(sdk.excludeAttachmentFilePatterns()))
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
                        ListConverter.toModel(sdk.customKnowledgeArticleTypeConfigurations(),
                                SalesforceConverter::toModelSalesforceCustomKnowledgeArticleTypeConfiguration))
                .includedStates(StringListConverter.toModel(sdk.includedStatesAsStrings()))
                .build();
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
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .build();
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
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .build();

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
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .build();
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
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .includeFilterTypes(StringListConverter.toModel(sdk.includeFilterTypesAsStrings()))
                .build();
    }

}
