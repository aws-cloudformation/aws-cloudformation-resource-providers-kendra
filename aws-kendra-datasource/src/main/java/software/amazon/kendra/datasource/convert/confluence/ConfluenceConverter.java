package software.amazon.kendra.datasource.convert.confluence;

import software.amazon.kendra.datasource.ConfluenceConfiguration;

/**
 * ConfluenceConverter converts between the ConfluenceConverter
 * objects in the generated RPK and the AWS SDK.
 *
 * @see ConfluenceModelToSdkConverter
 */
public class ConfluenceConverter {

    public static software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration toSdkDataSourceConfiguration(
            ConfluenceConfiguration model) {

        return ConfluenceModelToSdkConverter.toSdkDataSourceConfiguration(model);
    }

    public static software.amazon.kendra.datasource.DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration sdk) {

        return ConfluenceSdkToModelConverter.toModelDataSourceConfiguration(sdk);
    }
}
