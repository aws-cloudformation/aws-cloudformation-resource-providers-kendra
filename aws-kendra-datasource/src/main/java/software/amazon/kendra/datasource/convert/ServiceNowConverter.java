package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.ServiceNowConfiguration;
import software.amazon.kendra.datasource.ServiceNowKnowledgeArticleConfiguration;
import software.amazon.kendra.datasource.ServiceNowServiceCatalogConfiguration;

public class ServiceNowConverter {

    public static software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration toSdkDataSourceConfiguration(ServiceNowConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                .builder()
                .hostUrl(model.getHostUrl())
                .secretArn(model.getSecretArn())
                .serviceNowBuildVersion(model.getServiceNowBuildVersion())
                .knowledgeArticleConfiguration(toSdk(model.getKnowledgeArticleConfiguration()))
                .serviceCatalogConfiguration(toSdk(model.getServiceCatalogConfiguration()))
                .build();
    }

    public static software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration toSdk(
            ServiceNowKnowledgeArticleConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration
                .builder()
                .crawlAttachments(model.getCrawlAttachments())
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .includeAttachmentFilePatterns(StringListConverter.toSdk(model.getIncludeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toSdk(model.getExcludeAttachmentFilePatterns()))
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ServiceNowServiceCatalogConfiguration toSdk(
            ServiceNowServiceCatalogConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ServiceNowServiceCatalogConfiguration
                .builder()
                .crawlAttachments(model.getCrawlAttachments())
                .documentDataFieldName(model.getDocumentDataFieldName())
                .documentTitleFieldName(model.getDocumentTitleFieldName())
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .includeAttachmentFilePatterns(StringListConverter.toSdk(model.getIncludeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toSdk(model.getExcludeAttachmentFilePatterns()))
                .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk) {
        return software.amazon.kendra.datasource.DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(toModel(sdk))
                .build();
    }

    public static ServiceNowConfiguration toModel(software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ServiceNowConfiguration
                .builder()
                .hostUrl(sdk.hostUrl())
                .secretArn(sdk.secretArn())
                .serviceNowBuildVersion(sdk.serviceNowBuildVersionAsString())
                .knowledgeArticleConfiguration(toModel(sdk.knowledgeArticleConfiguration()))
                .serviceCatalogConfiguration(toModel(sdk.serviceCatalogConfiguration()))
                .build();
    }

    private static ServiceNowKnowledgeArticleConfiguration toModel(software.amazon.awssdk.services.kendra.model.ServiceNowKnowledgeArticleConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ServiceNowKnowledgeArticleConfiguration
                .builder()
                .crawlAttachments(sdk.crawlAttachments())
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .includeAttachmentFilePatterns(StringListConverter.toModel(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toModel(sdk.excludeAttachmentFilePatterns()))
                .build();
    }

    private static ServiceNowServiceCatalogConfiguration toModel(software.amazon.awssdk.services.kendra.model.ServiceNowServiceCatalogConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ServiceNowServiceCatalogConfiguration
                .builder()
                .crawlAttachments(sdk.crawlAttachments())
                .documentDataFieldName(sdk.documentDataFieldName())
                .documentTitleFieldName(sdk.documentTitleFieldName())
                .includeAttachmentFilePatterns(StringListConverter.toModel(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toModel(sdk.excludeAttachmentFilePatterns()))
                .fieldMappings(ListConverter.toModel(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .build();
    }
}
