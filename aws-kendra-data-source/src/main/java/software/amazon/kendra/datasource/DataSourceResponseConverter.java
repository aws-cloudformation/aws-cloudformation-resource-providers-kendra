package software.amazon.kendra.datasource;

import com.google.common.collect.Sets;
import software.amazon.awssdk.services.kendra.model.DataSourceType;

public class DataSourceResponseConverter {

  static DataSourceConfiguration getDataSourceConfiguration(
    final software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration,
    String dataSourceType) {
    if (DataSourceType.S3.toString().equals(dataSourceType)) {
      return getS3DataSourceConfiguration(dataSourceConfiguration.s3Configuration());
    }
    else {
      return null;
    }
  }

  static DataSourceConfiguration getS3DataSourceConfiguration(
    software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration s3DataSourceConfiguration) {
    return DataSourceConfiguration.builder()
      .s3Configuration(S3DataSourceConfiguration.builder()
      .bucketName(s3DataSourceConfiguration.bucketName())
      .inclusionPrefixes(Sets.newHashSet(s3DataSourceConfiguration.inclusionPrefixes()))
      .exclusionPatterns(Sets.newHashSet(s3DataSourceConfiguration.exclusionPatterns()))
      .accessControlListConfiguration(getAccessControlListConfiguration(s3DataSourceConfiguration.accessControlListConfiguration()))
      .documentsMetadataConfiguration(getDocumentsMetadataConfiguration(s3DataSourceConfiguration.documentsMetadataConfiguration()))
      .build()
    )
    .build();
  }

  static DocumentsMetadataConfiguration getDocumentsMetadataConfiguration(
    software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration documentsMetadataConfiguration) {
    if (documentsMetadataConfiguration == null) {
        return null;
    }
    return DocumentsMetadataConfiguration.builder()
      .s3Prefix(documentsMetadataConfiguration.s3Prefix())
      .build();
  }

  static AccessControlListConfiguration getAccessControlListConfiguration(
    software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration accessControlListConfiguration) {
    if(accessControlListConfiguration == null) {
      return null;
    }
    return AccessControlListConfiguration.builder()
      .keyPath(accessControlListConfiguration.keyPath())
      .build();
  }


}
