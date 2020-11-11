package software.amazon.kendra.datasource.convert.confluence;

import software.amazon.kendra.datasource.ConfluenceAttachmentConfiguration;
import software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping;
import software.amazon.kendra.datasource.ConfluenceBlogConfiguration;
import software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping;
import software.amazon.kendra.datasource.ConfluenceConfiguration;
import software.amazon.kendra.datasource.ConfluencePageConfiguration;
import software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping;
import software.amazon.kendra.datasource.ConfluenceSpaceConfiguration;
import software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping;
import software.amazon.kendra.datasource.convert.DataSourceVpcConfigurationConverter;
import software.amazon.kendra.datasource.convert.ListConverter;
import software.amazon.kendra.datasource.convert.StringListConverter;

/**
 * ConfluenceModelToSdkConverter is responsible for converting the AWS SDK model to the
 * RPK model generated via the aws-kendra-datasource.json schema.
 *
 * @see software.amazon.kendra.datasource.Translator
 */
public class ConfluenceSdkToModelConverter {

    public static software.amazon.kendra.datasource.DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration sdk) {

        return software.amazon.kendra.datasource.DataSourceConfiguration
            .builder()
            .confluenceConfiguration(confluenceConfigurationToModel(sdk))
            .build();
    }

    private static ConfluenceConfiguration confluenceConfigurationToModel(
            software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceConfiguration.builder()
            .serverUrl(sdk.serverUrl())
            .secretArn(sdk.secretArn())
            .version(sdk.versionAsString())
            .vpcConfiguration(DataSourceVpcConfigurationConverter.toModel(sdk.vpcConfiguration()))
            .spaceConfiguration(spaceConfigurationToModel(sdk.spaceConfiguration()))
            .pageConfiguration(pageConfigurationToModel(sdk.pageConfiguration()))
            .blogConfiguration(blogConfigurationToModel(sdk.blogConfiguration()))
            .attachmentConfiguration(attachmentConfigurationToModel(sdk.attachmentConfiguration()))
            .inclusionPatterns(StringListConverter.toModel(sdk.inclusionPatterns()))
            .exclusionPatterns(StringListConverter.toModel(sdk.exclusionPatterns()))
            .build();
    }

    private static ConfluenceSpaceConfiguration spaceConfigurationToModel(
            software.amazon.awssdk.services.kendra.model.ConfluenceSpaceConfiguration sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceSpaceConfiguration.builder()
            .crawlArchivedSpaces(sdk.crawlArchivedSpaces())
            .crawlPersonalSpaces(sdk.crawlPersonalSpaces())
            .spaceFieldMappings(ListConverter.toModel(sdk.spaceFieldMappings(), ConfluenceSdkToModelConverter::spaceFieldMappingToModel))
            .includeSpaces(StringListConverter.toModel(sdk.includeSpaces()))
            .excludeSpaces(StringListConverter.toModel(sdk.excludeSpaces()))
            .build();
    }

    private static ConfluenceSpaceToIndexFieldMapping spaceFieldMappingToModel(
            software.amazon.awssdk.services.kendra.model.ConfluenceSpaceToIndexFieldMapping sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceSpaceToIndexFieldMapping.builder()
            .indexFieldName(sdk.indexFieldName())
            .dataSourceFieldName(sdk.dataSourceFieldNameAsString())
            .dateFieldFormat(sdk.dateFieldFormat())
            .build();
    }

    private static ConfluencePageConfiguration pageConfigurationToModel(
            software.amazon.awssdk.services.kendra.model.ConfluencePageConfiguration sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluencePageConfiguration.builder()
            .pageFieldMappings(ListConverter.toModel(sdk.pageFieldMappings(), ConfluenceSdkToModelConverter::pageFieldMappingToModel))
            .build();
    }

    private static ConfluencePageToIndexFieldMapping pageFieldMappingToModel(
            software.amazon.awssdk.services.kendra.model.ConfluencePageToIndexFieldMapping sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluencePageToIndexFieldMapping.builder()
            .indexFieldName(sdk.indexFieldName())
            .dataSourceFieldName(sdk.dataSourceFieldNameAsString())
            .dateFieldFormat(sdk.dateFieldFormat())
            .build();
    }

    private static ConfluenceBlogConfiguration blogConfigurationToModel(
            software.amazon.awssdk.services.kendra.model.ConfluenceBlogConfiguration sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceBlogConfiguration.builder()
            .blogFieldMappings(ListConverter.toModel(sdk.blogFieldMappings(), ConfluenceSdkToModelConverter::blogFieldMappingToModel))
            .build();
    }

    private static ConfluenceBlogToIndexFieldMapping blogFieldMappingToModel(
        software.amazon.awssdk.services.kendra.model.ConfluenceBlogToIndexFieldMapping sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceBlogToIndexFieldMapping.builder()
            .indexFieldName(sdk.indexFieldName())
            .dataSourceFieldName(sdk.dataSourceFieldNameAsString())
            .dateFieldFormat(sdk.dateFieldFormat())
            .build();
    }

    private static ConfluenceAttachmentConfiguration attachmentConfigurationToModel(
            software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentConfiguration sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceAttachmentConfiguration.builder()
            .crawlAttachments(sdk.crawlAttachments())
            .attachmentFieldMappings(
                ListConverter.toModel(sdk.attachmentFieldMappings(), ConfluenceSdkToModelConverter::attachmentFieldMappingToModel))
            .build();
    }

    private static ConfluenceAttachmentToIndexFieldMapping attachmentFieldMappingToModel(
        software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentToIndexFieldMapping sdk) {

        if (sdk == null) {
            return null;
        }

        return ConfluenceAttachmentToIndexFieldMapping.builder()
            .indexFieldName(sdk.indexFieldName())
            .dataSourceFieldName(sdk.dataSourceFieldNameAsString())
            .dateFieldFormat(sdk.dateFieldFormat())
            .build();
    }
}
