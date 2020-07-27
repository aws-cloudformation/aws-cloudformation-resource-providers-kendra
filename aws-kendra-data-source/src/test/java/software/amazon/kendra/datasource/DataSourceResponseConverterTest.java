package software.amazon.kendra.datasource;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSourceResponseConverterTest {

  @Test
  public void testGetS3DataSourceConfiguration() {

    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
        .s3Configuration(software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder()
          .bucketName("testBucket")
          .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
          .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
          .accessControlListConfiguration(software.amazon.awssdk.services.kendra.model.AccessControlListConfiguration.builder()
            .keyPath("testKeyPath")
            .build())
          .documentsMetadataConfiguration(software.amazon.awssdk.services.kendra.model.DocumentsMetadataConfiguration.builder()
            .s3Prefix("testS3Prefix")
            .build())
          .build())
        .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
    .s3Configuration(S3DataSourceConfiguration.builder()
      .bucketName("testBucket")
      .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
      .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
      .accessControlListConfiguration(AccessControlListConfiguration.builder()
        .keyPath("testKeyPath")
        .build())
      .documentsMetadataConfiguration(DocumentsMetadataConfiguration.builder()
        .s3Prefix("testS3Prefix")
        .build())
      .build())
    .build() ;

    assertThat(DataSourceResponseConverter.getDataSourceConfiguration(dataSourceConfiguration, "S3"))
      .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testGetDataSourceConfiguration_WithInvalidType() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder().build();
     assertThat(DataSourceRequestConverter.getDataSourceConfiguration(dataSourceConfiguration, "Invalid"))
      .isEqualTo(null);
  }

  @Test
  public void testGetDataSourceConfiguration_WithNullAclAndDocumentMetadataConfiguration() {

   software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
        .s3Configuration(software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder()
          .bucketName("testBucket")
          .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
          .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
          .build())
        .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
    .s3Configuration(S3DataSourceConfiguration.builder()
      .bucketName("testBucket")
      .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
      .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
      .build())
    .build() ;

    assertThat(DataSourceResponseConverter.getDataSourceConfiguration(dataSourceConfiguration, "S3"))
      .isEqualTo(expectedDataSourceConfiguration);
  }

}
