package software.amazon.kendra.datasource;


import software.amazon.awssdk.services.kendra.model.DataSourceType;

public class DataSourceRequestConverter {

  static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration getDataSourceConfiguration(
    final DataSourceConfiguration dataSourceConfiguration, String dataSourceType) {
    if (DataSourceType.S3.toString().equals(dataSourceType)) {
      return getS3DataSourceConfiguration(dataSourceConfiguration.getS3Configuration());
    }
    else {
      return null;
    }
  }

  static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration getS3DataSourceConfiguration(
    S3DataSourceConfiguration s3DataSourceConfiguration) {
    return software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
    .s3Configuration(software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder()
      .bucketName(s3DataSourceConfiguration.getBucketName())
      .inclusionPrefixes(s3DataSourceConfiguration.getInclusionPrefixes())
      .exclusionPatterns(s3DataSourceConfiguration.getExclusionPatterns())
      .accessControlListConfiguration(getAccessControlListConfiguration(s3DataSourceConfiguration.getAccessControlListConfiguration()))
      .documentsMetadataConfiguration(getDocumentsMetadataConfiguration(s3DataSourceConfiguration.getDocumentsMetadataConfiguration()))
      .build()
    )
    .build();
  }

  static software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration getDocumentsMetadataConfiguration(
    DocumentsMetadataConfiguration documentsMetadataConfiguration) {
    if (documentsMetadataConfiguration == null) {
        return null;
    }
    return software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration.builder()
      .s3Prefix(documentsMetadataConfiguration.getS3Prefix())
      .build();
  }

  static software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration getAccessControlListConfiguration(
    AccessControlListConfiguration accessControlListConfiguration) {
    if(accessControlListConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration.builder()
      .keyPath(accessControlListConfiguration.getKeyPath())
      .build();
  }
}
