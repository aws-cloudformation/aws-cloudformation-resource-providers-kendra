package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceVpcConfiguration;

/**
 * DataSourceVpcConfigurationConverter converts between the DataSourceVpcConfiguration
 * objects in the generated RPK and the AWS SDK.
 */
public class DataSourceVpcConfigurationConverter {

    public static software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration toSdk(DataSourceVpcConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration
            .builder()
            .subnetIds(StringListConverter.toSdk(model.getSubnetIds()))
            .securityGroupIds(StringListConverter.toSdk(model.getSecurityGroupIds()))
            .build();
    }

    public static DataSourceVpcConfiguration toModel(software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return DataSourceVpcConfiguration
            .builder()
            .securityGroupIds(StringListConverter.toModel(sdk.securityGroupIds()))
            .subnetIds(StringListConverter.toModel(sdk.subnetIds()))
            .build();
    }
}
