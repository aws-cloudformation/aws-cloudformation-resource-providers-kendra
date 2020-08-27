package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DataSourceVpcConfiguration;
import software.amazon.kendra.datasource.SharePointConfiguration;

public class SharePointConverter {

  public static software.amazon.awssdk.services.kendra.model.SharePointConfiguration toSdk(SharePointConfiguration model) {
    if (model == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.SharePointConfiguration.builder()
            .sharePointVersion(model.getSharePointVersion())
            .urls(model.getUrls())
            .secretArn(model.getSecretArn())
            .crawlAttachments(model.getCrawlAttachments())
            .useChangeLog(model.getUseChangeLog())
            .inclusionPatterns(model.getInclusionPatterns())
            .exclusionPatterns(model.getExclusionPatterns())
            .vpcConfiguration(sdkVpcConfiguration(model.getVpcConfiguration()))
            .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
            .documentTitleFieldName(model.getDocumentTitleFieldName())
            .build();
  }

  public static DataSourceConfiguration toModelDataSourceConfiguration(
          software.amazon.awssdk.services.kendra.model.SharePointConfiguration sharePointConfiguration) {
    return DataSourceConfiguration.builder()
            .sharePointConfiguration(toModel(sharePointConfiguration))
            .build();
  }

  private static SharePointConfiguration toModel(software.amazon.awssdk.services.kendra.model.SharePointConfiguration sdk) {
    if (sdk == null) {
      return null;
    }
    return SharePointConfiguration.builder()
            .sharePointVersion(sdk.sharePointVersion().toString())
            .urls(sdk.urls())
            .secretArn(sdk.secretArn())
            .crawlAttachments(sdk.crawlAttachments())
            .useChangeLog(sdk.useChangeLog())
            .inclusionPatterns(sdk.inclusionPatterns())
            .exclusionPatterns(sdk.exclusionPatterns())
            .vpcConfiguration(modelVpcConfiguration(sdk.vpcConfiguration()))
            .fieldMappings(ListConverter.toModel(sdk.fieldMappings(), FieldMappingConverter::toModel))
            .documentTitleFieldName(sdk.documentTitleFieldName())
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
