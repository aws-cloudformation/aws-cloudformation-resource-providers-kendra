package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.WorkDocsConfiguration;

public class WorkDocsConverter {

    public static software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration toSdkDataSourceConfiguration(WorkDocsConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
            .organizationId(model.getOrganizationId())
            .crawlComments(model.getCrawlComments())
            .useChangeLog(model.getUseChangeLog())
            .inclusionPatterns(StringListConverter.toSdk(model.getInclusionPatterns()))
            .exclusionPatterns(StringListConverter.toSdk(model.getExclusionPatterns()))
            .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
            .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration workDocsConfiguration) {
        return DataSourceConfiguration.builder()
            .workDocsConfiguration(toModel(workDocsConfiguration))
            .build();
    }

    private static WorkDocsConfiguration toModel(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return WorkDocsConfiguration.builder()
            .organizationId(sdk.organizationId())
            .crawlComments(sdk.crawlComments())
            .useChangeLog(sdk.useChangeLog())
            .inclusionPatterns(StringListConverter.toModel(sdk.inclusionPatterns()))
            .exclusionPatterns(StringListConverter.toModel(sdk.exclusionPatterns()))
            .fieldMappings(ListConverter.toModel(sdk.fieldMappings(), FieldMappingConverter::toModel))
            .build();
    }
}
