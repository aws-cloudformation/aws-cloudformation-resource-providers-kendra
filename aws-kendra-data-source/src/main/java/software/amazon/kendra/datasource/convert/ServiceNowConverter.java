package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.ServiceNowConfiguration;
import software.amazon.kendra.datasource.ServiceNowKnowledgeArticleConfiguration;

public class ServiceNowConverter {
    public static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
            ServiceNowConfiguration model) {
        return software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .serviceNowConfiguration(toSdk(model))
                .build();
    }

    public static software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration toSdk(ServiceNowConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                .builder()
                .hostUrl(model.getHostUrl())
                .secretArn(model.getSecretArn())
                .serviceNowBuildVersion(model.getServiceNowBuildVersion())
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
                .fieldMappings(FieldMappingConverter.toModel(sdk.fieldMappings()))
                .includeAttachmentFilePatterns(StringListConverter.toModelStringList(sdk.includeAttachmentFilePatterns()))
                .excludeAttachmentFilePatterns(StringListConverter.toModelStringList(sdk.excludeAttachmentFilePatterns()))
                .build();
    }
}
