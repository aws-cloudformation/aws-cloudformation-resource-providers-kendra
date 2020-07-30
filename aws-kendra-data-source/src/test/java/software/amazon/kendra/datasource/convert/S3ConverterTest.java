package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.kendra.datasource.AccessControlListConfiguration;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DocumentsMetadataConfiguration;
import software.amazon.kendra.datasource.S3DataSourceConfiguration;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class S3ConverterTest {

  @Test
  public void testSdkDataSourceConfiguration() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
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

    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expectedDataSourceConfiguration =
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

    assertThat(S3Converter.sdkDataSourceConfiguration(dataSourceConfiguration.getS3Configuration()))
      .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration_WithNullAclAndDocumentMetadataConfiguration() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
    .s3Configuration(S3DataSourceConfiguration.builder()
      .bucketName("testBucket")
      .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
      .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
      .build())
    .build() ;

    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expectedDataSourceConfiguration =
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
    .s3Configuration(software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration.builder()
      .bucketName("testBucket")
      .inclusionPrefixes(Arrays.asList("testInclusionPrefix"))
      .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
      .build())
    .build();

     assertThat(S3Converter.sdkDataSourceConfiguration(dataSourceConfiguration.getS3Configuration()))
      .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration() {

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

    assertThat(S3Converter.modelDataSourceConfiguration(dataSourceConfiguration.s3Configuration()))
      .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration_WithNullAclAndDocumentMetadataConfiguration() {

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

    assertThat(S3Converter.modelDataSourceConfiguration(dataSourceConfiguration.s3Configuration()))
      .isEqualTo(expectedDataSourceConfiguration);
  }
}
