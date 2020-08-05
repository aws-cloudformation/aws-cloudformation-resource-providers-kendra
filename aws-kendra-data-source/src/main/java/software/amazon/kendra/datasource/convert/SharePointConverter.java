package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DataSourceVpcConfiguration;
import software.amazon.kendra.datasource.SharePointConfiguration;

public class SharePointConverter {

  public static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
          SharePointConfiguration sharePointConfiguration) {
    return software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
            .sharePointConfiguration(software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
                    .sharePointVersion(sharePointConfiguration.getSharePointVersion())
                    .urls(sharePointConfiguration.getUrls())
                    .secretArn(sharePointConfiguration.getSecretArn())
                    .crawlAttachments(sharePointConfiguration.getCrawlAttachments())
                    .useChangeLog(sharePointConfiguration.getUseChangeLog())
                    .inclusionPatterns(sharePointConfiguration.getInclusionPatterns())
                    .exclusionPatterns(sharePointConfiguration.getExclusionPatterns())
                    .vpcConfiguration(sdkVpcConfiguration(sharePointConfiguration.getVpcConfiguration()))
                    .fieldMappings(ListConverter.toSdk(sharePointConfiguration.getFieldMappings(), FieldMappingConverter::toSdk))
                    .documentTitleFieldName(sharePointConfiguration.getDocumentTitleFieldName())
                    .build())
            .build();
  }

  public static DataSourceConfiguration toModelDataSourceConfiguration(
          software.amazon.awssdk.services.kendra.model.SharePointConfiguration sharePointConfiguration) {
    return DataSourceConfiguration.builder()
            .sharePointConfiguration(SharePointConfiguration.builder()
                    .sharePointVersion(sharePointConfiguration.sharePointVersion().toString())
                    .urls(sharePointConfiguration.urls())
                    .secretArn(sharePointConfiguration.secretArn())
                    .crawlAttachments(sharePointConfiguration.crawlAttachments())
                    .useChangeLog(sharePointConfiguration.useChangeLog())
                    .inclusionPatterns(sharePointConfiguration.inclusionPatterns())
                    .exclusionPatterns(sharePointConfiguration.exclusionPatterns())
                    .vpcConfiguration(modelVpcConfiguration(sharePointConfiguration.vpcConfiguration()))
                    .fieldMappings(ListConverter.toModel(sharePointConfiguration.fieldMappings(), FieldMappingConverter::toModel))
                    .documentTitleFieldName(sharePointConfiguration.documentTitleFieldName())
                    .build())
            .build();
  }

  static software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration sdkVpcConfiguration(
          DataSourceVpcConfiguration dataSourceVpcConfiguration) {
    if (dataSourceVpcConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration.builder()
      .securityGroupIds(dataSourceVpcConfiguration.getSecurityGroupIds())
      .subnetIds(dataSourceVpcConfiguration.getSubnetIds())
      .build();
  }

  static DataSourceVpcConfiguration modelVpcConfiguration(software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration
    dataSourceVpcConfiguration) {
    if (dataSourceVpcConfiguration == null) {
      return null;
    }
    return DataSourceVpcConfiguration.builder()
      .securityGroupIds(dataSourceVpcConfiguration.securityGroupIds())
      .subnetIds(dataSourceVpcConfiguration.subnetIds())
      .build();
  }

}
