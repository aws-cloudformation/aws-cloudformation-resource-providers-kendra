package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.SharePointVersion;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DataSourceToIndexFieldMapping;
import software.amazon.kendra.datasource.DataSourceVpcConfiguration;
import software.amazon.kendra.datasource.SharePointConfiguration;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SharePointCoverterTest {

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
        .fieldMappings(Arrays.asList(DataSourceToIndexFieldMapping.builder()
          .dataSourceFieldName("testDataSourceFieldName")
          .dateFieldFormat("testDateFieldFormat")
          .indexFieldName("testIndexFieldName")
          .build()))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expectedDataSourceConfiguration =
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
        .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
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
        .inclusionPatterns(Arrays.asList("testInclusionPatterns"))
        .exclusionPatterns(Arrays.asList("testExclusionPatterns"))
        .documentTitleFieldName("testDocumentTitleFieldName")
        .build())
      .build();

    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration expectedDataSourceConfiguration =
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
        .fieldMappings(Arrays.asList())
        .build())
      .build();

     assertThat(SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration()))
    .isEqualTo(expectedDataSourceConfiguration);
  }

}
