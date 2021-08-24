package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.kendra.model.SharePointVersion;
import software.amazon.kendra.datasource.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SharePointConverterTest {

  @Test
  public void testSdkDataSourceConfiguration() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
      .sharePointConfiguration(SharePointConfiguration.builder()
        .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
        .urls(Arrays.asList("www.sharepoint.com"))
        .secretArn("secretArn")
        .crawlAttachments(true)
        .useChangeLog(true)
        .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
        .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
        .vpcConfiguration( DataSourceVpcConfiguration.builder()
          .securityGroupIds(Arrays.asList("testSecurityGroupId"))
          .subnetIds(Arrays.asList("testSubnetId"))
          .build())
        .disableLocalGroups(true)
        .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
          .dataSourceFieldName("testDataSourceFieldName")
          .dateFieldFormat("testDateFieldFormat")
          .indexFieldName("testIndexFieldName")
          .build()))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
        software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
          .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
          .urls(Arrays.asList("www.sharepoint.com"))
          .secretArn("secretArn")
                .crawlAttachments(true)
                .useChangeLog(true)
                .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                        .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                        .subnetIds(Arrays.asList("testSubnetId"))
                        .build())
                .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                        .dataSourceFieldName("testDataSourceFieldName")
                        .dateFieldFormat("testDateFieldFormat")
                        .indexFieldName("testIndexFieldName")
                        .build()))
                .documentTitleFieldName("testDocumentTitleFieldName")
                .disableLocalGroups(true)
                .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration_2013() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .disableLocalGroups(true)
                    .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .sslCertificateS3Path(S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build())
            .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .disableLocalGroups(true)
                    .sslCertificateS3Path(software.amazon.awssdk.services.kendra.model.S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration_2016() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .disableLocalGroups(true)
                    .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .sslCertificateS3Path(S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build())
            .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .disableLocalGroups(true)
                    .sslCertificateS3Path(software.amazon.awssdk.services.kendra.model.S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration_withNullVpcConfigurationAndNullFieldMappings() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
      .sharePointConfiguration(SharePointConfiguration.builder()
        .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
        .urls(Arrays.asList("www.sharepoint.com"))
        .secretArn("secretArn")
        .crawlAttachments(true)
        .useChangeLog(true)
        .disableLocalGroups(false)
        .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
        .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
                    .urls(Arrays.asList("www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .disableLocalGroups(false)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration2013_withNullSslCertAndNullFieldMappings() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .disableLocalGroups(true)
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .build())
            .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .disableLocalGroups(true)
                    .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testSdkDataSourceConfiguration2016_withNullSslCertAndNullFieldMappings() {
    DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .disableLocalGroups(true)
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .build())
            .build();

    software.amazon.awssdk.services.kendra.model.SharePointConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("http://www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .disableLocalGroups(true)
                    .build();

    assertThat(SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
        .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
          .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
          .urls(Arrays.asList("www.sharepoint.com"))
          .secretArn("secretArn")
          .crawlAttachments(true)
          .useChangeLog(true)
          .disableLocalGroups(true)
          .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
          .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
          .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
            .subnetIds(Arrays.asList("testSubnetId"))
            .build())
          .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
            .dataSourceFieldName("testDataSourceFieldName")
            .dateFieldFormat("testDateFieldFormat")
            .indexFieldName("testIndexFieldName")
            .build()))
          .documentTitleFieldName("testDocumentTitleFieldName")
          .build())
        .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
      .sharePointConfiguration(SharePointConfiguration.builder()
        .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
        .urls(Arrays.asList("www.sharepoint.com"))
        .secretArn("secretArn")
        .crawlAttachments(true)
        .useChangeLog(true)
        .disableLocalGroups(true)
        .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
        .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
        .vpcConfiguration( DataSourceVpcConfiguration.builder()
          .securityGroupIds(Arrays.asList("testSecurityGroupId"))
          .subnetIds(Arrays.asList("testSubnetId"))
          .build())
        .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
          .dataSourceFieldName("testDataSourceFieldName")
          .dateFieldFormat("testDateFieldFormat")
          .indexFieldName("testIndexFieldName")
          .build()))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

     assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
    .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration_2013() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                    .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                            .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                            .urls(Arrays.asList("www.sharepoint.com"))
                            .secretArn("secretArn")
                            .crawlAttachments(true)
                            .useChangeLog(true)
                            .disableLocalGroups(true)
                            .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                            .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                            .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                                    .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                                    .subnetIds(Arrays.asList("testSubnetId"))
                                    .build())
                            .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                                    .dataSourceFieldName("testDataSourceFieldName")
                                    .dateFieldFormat("testDateFieldFormat")
                                    .indexFieldName("testIndexFieldName")
                                    .build()))
                            .documentTitleFieldName("testDocumentTitleFieldName")
                            .sslCertificateS3Path(software.amazon.awssdk.services.kendra.model.S3Path.builder()
                                    .bucket("bucket")
                                    .key("key")
                                    .build())
                            .build())
                    .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .disableLocalGroups(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .sslCertificateS3Path(S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build())
            .build();

    assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration_2016() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                    .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                            .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                            .urls(Arrays.asList("www.sharepoint.com"))
                            .secretArn("secretArn")
                            .crawlAttachments(true)
                            .useChangeLog(true)
                            .disableLocalGroups(true)
                            .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                            .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                            .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                                    .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                                    .subnetIds(Arrays.asList("testSubnetId"))
                                    .build())
                            .fieldMappings(Arrays.asList(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                                    .dataSourceFieldName("testDataSourceFieldName")
                                    .dateFieldFormat("testDateFieldFormat")
                                    .indexFieldName("testIndexFieldName")
                                    .build()))
                            .documentTitleFieldName("testDocumentTitleFieldName")
                            .sslCertificateS3Path(software.amazon.awssdk.services.kendra.model.S3Path.builder()
                                    .bucket("bucket")
                                    .key("key")
                                    .build())
                            .build())
                    .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .disableLocalGroups(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName("testDataSourceFieldName")
                            .dateFieldFormat("testDateFieldFormat")
                            .indexFieldName("testIndexFieldName")
                            .build()))
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .sslCertificateS3Path(S3Path.builder()
                            .bucket("bucket")
                            .key("key")
                            .build())
                    .build())
            .build();

    assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration_withNullVpcConfigurationAndNullFieldMappings() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
        .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
          .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
          .urls(Arrays.asList("www.sharepoint.com"))
          .secretArn("secretArn")
          .crawlAttachments(true)
          .useChangeLog(true)
          .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
          .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
          .documentTitleFieldName("testDocumentTitleFieldName")
          .build())
        .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
      .sharePointConfiguration(SharePointConfiguration.builder()
        .sharePointVersion(SharePointVersion.SHAREPOINT_ONLINE.toString())
        .urls(Arrays.asList("www.sharepoint.com"))
        .secretArn("secretArn")
        .crawlAttachments(true)
        .useChangeLog(true)
        .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
        .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

     assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
    .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration2013_withNullSslCertAndNullFieldMappings() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                    .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                            .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                            .urls(Arrays.asList("www.sharepoint.com"))
                            .secretArn("secretArn")
                            .crawlAttachments(true)
                            .useChangeLog(true)
                            .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                            .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                            .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                                    .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                                    .subnetIds(Arrays.asList("testSubnetId"))
                                    .build())
                            .documentTitleFieldName("testDocumentTitleFieldName")
                            .build())
                    .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2013.toString())
                    .urls(Arrays.asList("www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .build())
            .build();

    assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }

  @Test
  public void testModelDataSourceConfiguration2016_withNullSslCertAndNullFieldMappings() {
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                    .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                            .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                            .urls(Arrays.asList("www.sharepoint.com"))
                            .secretArn("secretArn")
                            .crawlAttachments(true)
                            .useChangeLog(true)
                            .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                            .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                            .vpcConfiguration( software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
                                    .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                                    .subnetIds(Arrays.asList("testSubnetId"))
                                    .build())
                            .documentTitleFieldName("testDocumentTitleFieldName")
                            .build())
                    .build();

    DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(SharePointVersion.SHAREPOINT_2016.toString())
                    .urls(Arrays.asList("www.sharepoint.com"))
                    .secretArn("secretArn")
                    .crawlAttachments(true)
                    .useChangeLog(true)
                    .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
                    .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
                    .vpcConfiguration( DataSourceVpcConfiguration.builder()
                            .securityGroupIds(Arrays.asList("testSecurityGroupId"))
                            .subnetIds(Arrays.asList("testSubnetId"))
                            .build())
                    .documentTitleFieldName("testDocumentTitleFieldName")
                    .build())
            .build();

    assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
  }
}
