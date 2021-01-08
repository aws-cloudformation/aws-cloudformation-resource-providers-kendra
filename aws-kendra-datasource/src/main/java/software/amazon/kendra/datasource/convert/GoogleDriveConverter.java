package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.GoogleDriveConfiguration;

/**
 * GoogleDriveConverter is responsible for converting between the RPK models used
 * by CloudFormation and those used by the AWS Sdk.
 *
 * @see software.amazon.kendra.datasource.Translator - where this class is used
 */
public class GoogleDriveConverter {

    /**
     * Given an RPK model, return the SDK representation of that model.
     * @param model the RPK model
     * @return The sdk representation
     */
    public static software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration toSdkDataSourceConfiguration(GoogleDriveConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration.builder()
            .secretArn(model.getSecretArn())
            .inclusionPatterns(StringListConverter.toSdk(model.getInclusionPatterns()))
            .exclusionPatterns(StringListConverter.toSdk(model.getExclusionPatterns()))
            .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
            .excludeMimeTypes(StringListConverter.toSdk(model.getExcludeMimeTypes()))
            .excludeUserAccounts(StringListConverter.toSdk(model.getExcludeUserAccounts()))
            .excludeSharedDrives(StringListConverter.toSdk(model.getExcludeSharedDrives()))
            .build();
    }

    /**
     * Given an SDK model, return an RPK model representation of that model (wrapped in a DataSourceConfiguration)
     * @param sdk the sdk model
     * @return The rpk representation of the model in a DataSourceConfiguration
     */
    public static DataSourceConfiguration toModelDataSourceConfiguration(software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration sdk) {
        return DataSourceConfiguration.builder()
            .googleDriveConfiguration(toModel(sdk))
            .build();
    }

    /**
     * Converts an SDK GoogleDriveConfiguration to the same structure as an RPK model
     * @param sdk the sdk configuration
     * @return the same structure as an RPK model
     */
    private static GoogleDriveConfiguration toModel(software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return GoogleDriveConfiguration.builder()
            .secretArn(sdk.secretArn())
            .inclusionPatterns(StringListConverter.toModel(sdk.inclusionPatterns()))
            .exclusionPatterns(StringListConverter.toModel(sdk.exclusionPatterns()))
            .fieldMappings(ListConverter.toModel(sdk.fieldMappings(), FieldMappingConverter::toModel))
            .excludeMimeTypes(StringListConverter.toModel(sdk.excludeMimeTypes()))
            .excludeUserAccounts(StringListConverter.toModel(sdk.excludeUserAccounts()))
            .excludeSharedDrives(StringListConverter.toModel(sdk.excludeSharedDrives()))
            .build();
    }
}
