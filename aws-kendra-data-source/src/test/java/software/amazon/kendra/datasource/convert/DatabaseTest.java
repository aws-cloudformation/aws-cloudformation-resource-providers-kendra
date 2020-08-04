package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.AclConfiguration;
import software.amazon.awssdk.services.kendra.model.ColumnConfiguration;
import software.amazon.awssdk.services.kendra.model.ConnectionConfiguration;
import software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration;
import software.amazon.awssdk.services.kendra.model.DatabaseConfiguration;
import software.amazon.awssdk.services.kendra.model.DatabaseEngineType;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseTest {

    @Test
    void testToSdkEngineType() {
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .databaseEngineType(DatabaseEngineType.RDS_AURORA_MYSQL)
                .build();
        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .databaseEngineType(DatabaseEngineType.RDS_AURORA_MYSQL.toString())
                        .build();
        assertThat(DatabaseConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkConnectionConfiguration() {
        String databaseHost = "databaseHost";
        String databaseName = "databaseName";
        Integer databasePort = 1;
        String tableName = "tableName";
        String secretArn = "secretArn";
        ConnectionConfiguration sdkConnectionConfig = ConnectionConfiguration
                .builder()
                .databaseName(databaseName)
                .databaseHost(databaseHost)
                .databasePort(databasePort)
                .secretArn(secretArn)
                .tableName(tableName)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .connectionConfiguration(sdkConnectionConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .connectionConfiguration(software.amazon.kendra.datasource.ConnectionConfiguration
                                .builder()
                                .databaseHost(databaseHost)
                                .databaseName(databaseName)
                                .databasePort(databasePort)
                                .secretArn(secretArn)
                                .tableName(tableName)
                                .build())
                        .build();

        assertThat(DatabaseConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkVPCConfiguration() {
        List<String> securityGroupIds = Arrays.asList("0");
        List<String> subnetIds = Arrays.asList("1");
        DataSourceVpcConfiguration sdkVpcConfig = DataSourceVpcConfiguration
                .builder()
                .securityGroupIds(securityGroupIds)
                .subnetIds(subnetIds)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .vpcConfiguration(sdkVpcConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .vpcConfiguration(software.amazon.kendra.datasource.DataSourceVpcConfiguration
                                .builder()
                                .subnetIds(subnetIds)
                                .securityGroupIds(securityGroupIds)
                                .build())
                        .build();

        assertThat(DatabaseConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkColumnConfiguration() {
        String documentIdColumnName = "documentIdColumnName ";
        String documentDataColumnName = "documentDataColumnName ";
        String documentTitleColumnName = "documentTitleColumnName ";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dateFieldFormat";
        DataSourceToIndexFieldMapping dataSourceToIndexFieldMapping =
                DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        String cdcColumn = "c";
        ColumnConfiguration columnConfiguration = ColumnConfiguration
                .builder()
                .documentDataColumnName(documentDataColumnName)
                .documentIdColumnName(documentIdColumnName)
                .documentTitleColumnName(documentTitleColumnName)
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .changeDetectingColumns(Arrays.asList(cdcColumn))
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .columnConfiguration(columnConfiguration)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .columnConfiguration(
                                software.amazon.kendra.datasource.ColumnConfiguration
                                        .builder()
                                        .documentIdColumnName(documentIdColumnName)
                                        .documentDataColumnName(documentDataColumnName)
                                        .documentTitleColumnName(documentTitleColumnName)
                                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                                        .changeDetectingColumns(Arrays.asList(cdcColumn))
                                        .build())
                        .build();

        assertThat(DatabaseConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkAclConfig() {
        String columnName = "columnName";
        AclConfiguration sdkAclConfig = AclConfiguration
                .builder()
                .allowedGroupsColumnName(columnName)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .aclConfiguration(sdkAclConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .aclConfiguration(software.amazon.kendra.datasource.AclConfiguration
                                .builder()
                                .allowedGroupsColumnName(columnName)
                                .build())
                        .build();
        assertThat(DatabaseConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToModelEngineType() {
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .databaseEngineType(DatabaseEngineType.RDS_AURORA_MYSQL)
                .build();
        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .databaseEngineType(DatabaseEngineType.RDS_AURORA_MYSQL.toString())
                        .build();
        assertThat(DatabaseConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelConnectionConfiguration() {
        String databaseHost = "databaseHost";
        String databaseName = "databaseName";
        Integer databasePort = 1;
        String tableName = "tableName";
        String secretArn = "secretArn";
        ConnectionConfiguration sdkConnectionConfig = ConnectionConfiguration
                .builder()
                .databaseName(databaseName)
                .databaseHost(databaseHost)
                .databasePort(databasePort)
                .secretArn(secretArn)
                .tableName(tableName)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .connectionConfiguration(sdkConnectionConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .connectionConfiguration(software.amazon.kendra.datasource.ConnectionConfiguration
                                .builder()
                                .databaseHost(databaseHost)
                                .databaseName(databaseName)
                                .databasePort(databasePort)
                                .secretArn(secretArn)
                                .tableName(tableName)
                                .build())
                        .build();

        assertThat(DatabaseConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelVPCConfiguration() {
        List<String> securityGroupIds = Arrays.asList("0");
        List<String> subnetIds = Arrays.asList("1");
        DataSourceVpcConfiguration sdkVpcConfig = DataSourceVpcConfiguration
                .builder()
                .securityGroupIds(securityGroupIds)
                .subnetIds(subnetIds)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .vpcConfiguration(sdkVpcConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .vpcConfiguration(software.amazon.kendra.datasource.DataSourceVpcConfiguration
                                .builder()
                                .subnetIds(subnetIds)
                                .securityGroupIds(securityGroupIds)
                                .build())
                        .build();

        assertThat(DatabaseConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelColumnConfiguration() {
        String documentIdColumnName = "documentIdColumnName ";
        String documentDataColumnName = "documentDataColumnName ";
        String documentTitleColumnName = "documentTitleColumnName ";
        String dataSourceFieldName = "dataSourceFieldName";
        String indexFieldName = "indexFieldName";
        String dateFieldFormat = "dateFieldFormat";
        DataSourceToIndexFieldMapping dataSourceToIndexFieldMapping =
                DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        String cdcColumn = "c";
        ColumnConfiguration columnConfiguration = ColumnConfiguration
                .builder()
                .documentDataColumnName(documentDataColumnName)
                .documentIdColumnName(documentIdColumnName)
                .documentTitleColumnName(documentTitleColumnName)
                .fieldMappings(Arrays.asList(dataSourceToIndexFieldMapping))
                .changeDetectingColumns(Arrays.asList(cdcColumn))
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .columnConfiguration(columnConfiguration)
                .build();

        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping modelDataSourceToIndexFieldMapping =
                software.amazon.kendra.datasource.DataSourceToIndexFieldMapping
                        .builder()
                        .dataSourceFieldName(dataSourceFieldName)
                        .indexFieldName(indexFieldName)
                        .dateFieldFormat(dateFieldFormat)
                        .build();
        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .columnConfiguration(
                                software.amazon.kendra.datasource.ColumnConfiguration
                                        .builder()
                                        .documentIdColumnName(documentIdColumnName)
                                        .documentDataColumnName(documentDataColumnName)
                                        .documentTitleColumnName(documentTitleColumnName)
                                        .fieldMappings(Arrays.asList(modelDataSourceToIndexFieldMapping))
                                        .changeDetectingColumns(Arrays.asList(cdcColumn))
                                        .build())
                        .build();

        assertThat(DatabaseConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelAclConfig() {
        String columnName = "columnName";
        AclConfiguration sdkAclConfig = AclConfiguration
                .builder()
                .allowedGroupsColumnName(columnName)
                .build();
        DatabaseConfiguration sdk = DatabaseConfiguration
                .builder()
                .aclConfiguration(sdkAclConfig)
                .build();

        software.amazon.kendra.datasource.DatabaseConfiguration model =
                software.amazon.kendra.datasource.DatabaseConfiguration
                        .builder()
                        .aclConfiguration(software.amazon.kendra.datasource.AclConfiguration
                                .builder()
                                .allowedGroupsColumnName(columnName)
                                .build())
                        .build();
        assertThat(DatabaseConverter.toModel(sdk)).isEqualTo(model);
    }
}
