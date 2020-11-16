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
 * ConfluenceModelToSdkConverter is responsible for converting the RPK model
 * generated via the aws-kendra-datasource.json schema to the structure
 * that is expected for the AWS SDK.
 *
 * @see software.amazon.kendra.datasource.Translator
 */
public class ConfluenceModelToSdkConverter {

    public static software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration toSdkDataSourceConfiguration(
            ConfluenceConfiguration model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration.builder()
            .serverUrl(model.getServerUrl())
            .secretArn(model.getSecretArn())
            .version(model.getVersion())
            .spaceConfiguration(spaceConfigurationToSdk(model.getSpaceConfiguration()))
            .pageConfiguration(pageConfigurationToSdk(model.getPageConfiguration()))
            .blogConfiguration(blogConfigurationToSdk(model.getBlogConfiguration()))
            .attachmentConfiguration(attachmentConfigurationToSdk(model.getAttachmentConfiguration()))
            .vpcConfiguration(DataSourceVpcConfigurationConverter.toSdk(model.getVpcConfiguration()))
            .inclusionPatterns(StringListConverter.toSdk(model.getInclusionPatterns()))
            .exclusionPatterns(StringListConverter.toSdk(model.getExclusionPatterns()))
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceSpaceConfiguration spaceConfigurationToSdk(
            ConfluenceSpaceConfiguration model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceSpaceConfiguration.builder()
            .spaceFieldMappings(ListConverter.toSdk(model.getSpaceFieldMappings(), ConfluenceModelToSdkConverter::spaceFieldMappingToSdk))
            .crawlArchivedSpaces(model.getCrawlArchivedSpaces())
            .crawlPersonalSpaces(model.getCrawlPersonalSpaces())
            .includeSpaces(StringListConverter.toSdk(model.getIncludeSpaces()))
            .excludeSpaces(StringListConverter.toSdk(model.getExcludeSpaces()))
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceSpaceToIndexFieldMapping spaceFieldMappingToSdk(
            ConfluenceSpaceToIndexFieldMapping model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceSpaceToIndexFieldMapping.builder()
            .dataSourceFieldName(model.getDataSourceFieldName())
            .indexFieldName(model.getIndexFieldName())
            .dateFieldFormat(model.getDateFieldFormat())
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluencePageConfiguration pageConfigurationToSdk(
            ConfluencePageConfiguration model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluencePageConfiguration.builder()
            .pageFieldMappings(ListConverter.toSdk(model.getPageFieldMappings(), ConfluenceModelToSdkConverter::pageFieldMappingToSdk))
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluencePageToIndexFieldMapping pageFieldMappingToSdk(
            ConfluencePageToIndexFieldMapping model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluencePageToIndexFieldMapping.builder()
            .dataSourceFieldName(model.getDataSourceFieldName())
            .indexFieldName(model.getIndexFieldName())
            .dateFieldFormat(model.getDateFieldFormat())
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceBlogConfiguration blogConfigurationToSdk(
            ConfluenceBlogConfiguration model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceBlogConfiguration.builder()
            .blogFieldMappings(ListConverter.toSdk(model.getBlogFieldMappings(), ConfluenceModelToSdkConverter::blogFieldMappingToSdk))
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceBlogToIndexFieldMapping blogFieldMappingToSdk(
            ConfluenceBlogToIndexFieldMapping model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceBlogToIndexFieldMapping.builder()
            .dataSourceFieldName(model.getDataSourceFieldName())
            .indexFieldName(model.getIndexFieldName())
            .dateFieldFormat(model.getDateFieldFormat())
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentConfiguration attachmentConfigurationToSdk(
            ConfluenceAttachmentConfiguration model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentConfiguration.builder()
            .attachmentFieldMappings(ListConverter.toSdk(
                model.getAttachmentFieldMappings(), ConfluenceModelToSdkConverter::attachmentFieldMappingToSdk))
            .crawlAttachments(model.getCrawlAttachments())
            .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentToIndexFieldMapping attachmentFieldMappingToSdk(
            ConfluenceAttachmentToIndexFieldMapping model) {

        if (model == null) {
            return null;
        }

        return software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentToIndexFieldMapping.builder()
            .dataSourceFieldName(model.getDataSourceFieldName())
            .indexFieldName(model.getIndexFieldName())
            .dateFieldFormat(model.getDateFieldFormat())
            .build();
    }
}
