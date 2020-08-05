package software.amazon.kendra.datasource.convert;

import software.amazon.awssdk.services.kendra.model.DataSourceConfiguration;
import software.amazon.kendra.datasource.OneDriveConfiguration;
import software.amazon.kendra.datasource.OneDriveUsers;
import software.amazon.kendra.datasource.S3Path;

public class OneDriveConverter {
    public static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
            OneDriveConfiguration model) {
        return DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(software.amazon.awssdk.services.kendra.model.OneDriveConfiguration.builder().build())
                .build();
    }

    public static software.amazon.awssdk.services.kendra.model.OneDriveConfiguration toSdk(OneDriveConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.OneDriveConfiguration
                .builder()
                .secretArn(model.getSecretArn())
                .tenantDomain(model.getTenantDomain())
                .inclusionPatterns(StringListConverter.toSdk(model.getInclusionPatterns()))
                .exclusionPatterns(StringListConverter.toSdk(model.getExclusionPatterns()))
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
                .oneDriveUsers(toSdk(model.getOneDriveUsers()))
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.OneDriveUsers toSdk(OneDriveUsers model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.OneDriveUsers
                .builder()
                .oneDriveUserList(StringListConverter.toSdk(model.getOneDriveUserList()))
                .oneDriveUserS3Path(toSdk(model.getOneDriveUserS3Path()))
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.S3Path toSdk(S3Path model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.S3Path
                .builder()
                .bucket(model.getBucket())
                .key(model.getKey())
                .build();
    }

    public static software.amazon.kendra.datasource.DataSourceConfiguration toModelDataSourceConfiguration(software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk) {
        return software.amazon.kendra.datasource.DataSourceConfiguration
                .builder()
                .oneDriveConfiguration(OneDriveConfiguration.builder().build())
                .build();
    }

    public static OneDriveConfiguration toModel(software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return OneDriveConfiguration
                .builder()
                .secretArn(sdk.secretArn())
                .tenantDomain(sdk.tenantDomain())
                .inclusionPatterns(StringListConverter.toModel(sdk.inclusionPatterns()))
                .exclusionPatterns(StringListConverter.toModel(sdk.exclusionPatterns()))
                .oneDriveUsers(toModel(sdk.oneDriveUsers()))
                .build();
    }

    private static OneDriveUsers toModel(software.amazon.awssdk.services.kendra.model.OneDriveUsers sdk) {
        if (sdk == null) {
            return null;
        }
        return OneDriveUsers
                .builder()
                .oneDriveUserList(StringListConverter.toModel(sdk.oneDriveUserList()))
                .oneDriveUserS3Path(toModel(sdk.oneDriveUserS3Path()))
                .build();
    }

    private static S3Path toModel(software.amazon.awssdk.services.kendra.model.S3Path sdk) {
        if (sdk == null) {
            return null;
        }
        return S3Path
                .builder()
                .bucket(sdk.bucket())
                .key(sdk.key())
                .build();
    }
}
