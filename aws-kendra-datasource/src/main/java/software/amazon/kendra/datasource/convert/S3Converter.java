package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.AccessControlListConfiguration;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DocumentsMetadataConfiguration;
import software.amazon.kendra.datasource.S3DataSourceConfiguration;

public class S3Converter {

  public static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
          S3DataSourceConfiguration s3DataSourceConfiguration) {
    return software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
            .s3Configuration(toSdk(s3DataSourceConfiguration))
            .build();
  }

  private static software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration toSdk(S3DataSourceConfiguration model) {
    if (model == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder()
            .bucketName(model.getBucketName())
            .inclusionPrefixes(StringListConverter.toSdk(model.getInclusionPrefixes()))
            .exclusionPatterns(StringListConverter.toSdk(model.getExclusionPatterns()))
            .accessControlListConfiguration(sdkAccessControlListConfiguration(model.getAccessControlListConfiguration()))
            .documentsMetadataConfiguration(sdkDocumentsMetadataConfiguration(model.getDocumentsMetadataConfiguration()))
            .build();
  }

  public static DataSourceConfiguration toModelDataSourceConfiguration(
          software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration s3DataSourceConfiguration) {
    return DataSourceConfiguration.builder()
            .s3Configuration(S3DataSourceConfiguration.builder()
                    .bucketName(s3DataSourceConfiguration.bucketName())
                    .inclusionPrefixes(StringListConverter.toModel(s3DataSourceConfiguration.inclusionPrefixes()))
                    .exclusionPatterns(StringListConverter.toModel(s3DataSourceConfiguration.exclusionPatterns()))
                    .accessControlListConfiguration(modelAccessControlListConfiguration(s3DataSourceConfiguration.accessControlListConfiguration()))
                    .documentsMetadataConfiguration(modelDocumentsMetadataConfiguration(s3DataSourceConfiguration.documentsMetadataConfiguration()))
                    .build()
            )
            .build();
  }


  static software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration sdkDocumentsMetadataConfiguration(
          DocumentsMetadataConfiguration documentsMetadataConfiguration) {
    if (documentsMetadataConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration.builder()
      .s3Prefix(documentsMetadataConfiguration.getS3Prefix())
      .build();
  }

  static software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration sdkAccessControlListConfiguration(
    AccessControlListConfiguration accessControlListConfiguration) {
    if(accessControlListConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration.builder()
      .keyPath(accessControlListConfiguration.getKeyPath())
      .build();
  }

  static DocumentsMetadataConfiguration modelDocumentsMetadataConfiguration(
    software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration documentsMetadataConfiguration) {
    if (documentsMetadataConfiguration == null) {
        return null;
    }
    return DocumentsMetadataConfiguration.builder()
      .s3Prefix(documentsMetadataConfiguration.s3Prefix())
      .build();
  }

  static AccessControlListConfiguration modelAccessControlListConfiguration(
    software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration accessControlListConfiguration) {
    if(accessControlListConfiguration == null) {
      return null;
    }
    return AccessControlListConfiguration.builder()
      .keyPath(accessControlListConfiguration.keyPath())
      .build();
  }

}
