package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.AclConfiguration;
import software.amazon.kendra.datasource.ColumnConfiguration;
import software.amazon.kendra.datasource.ConnectionConfiguration;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DataSourceToIndexFieldMapping;
import software.amazon.kendra.datasource.DataSourceVpcConfiguration;
import software.amazon.kendra.datasource.DatabaseConfiguration;
import software.amazon.kendra.datasource.SqlConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseConverter {
    public static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
            DatabaseConfiguration model) {
        return software.amazon.awssdk.services.kendra.model.DataSourceConfiguration
                .builder()
                .databaseConfiguration(toSdk(model))
                .build();
    }

    public static software.amazon.awssdk.services.kendra.model.DatabaseConfiguration toSdk(
            DatabaseConfiguration model) {
        return software.amazon.awssdk.services.kendra.model.DatabaseConfiguration
                .builder()
                .databaseEngineType(model.getDatabaseEngineType())
                .connectionConfiguration(toSdk(model.getConnectionConfiguration()))
                .vpcConfiguration(toSdk(model.getVpcConfiguration()))
                .columnConfiguration(toSdk(model.getColumnConfiguration()))
                .aclConfiguration(toSdk(model.getAclConfiguration()))
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.ConnectionConfiguration toSdk(ConnectionConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ConnectionConfiguration
                .builder()
                .databaseHost(model.getDatabaseHost())
                .databasePort(model.getDatabasePort())
                .databaseName(model.getDatabaseName())
                .secretArn(model.getSecretArn())
                .tableName(model.getTableName())
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration toSdk(DataSourceVpcConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration
                .builder()
                .subnetIds(toSdkStringList(model.getSubnetIds()))
                .securityGroupIds(toSdkStringList(model.getSecurityGroupIds()))
                .build();
    }

    private static List<String> toSdkStringList(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return stringList.stream().collect(Collectors.toList());
    }

    private static software.amazon.awssdk.services.kendra.model.ColumnConfiguration toSdk(ColumnConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.ColumnConfiguration
                .builder()
                .documentIdColumnName(model.getDocumentIdColumnName())
                .documentDataColumnName(model.getDocumentDataColumnName())
                .documentTitleColumnName(model.getDocumentTitleColumnName())
                .changeDetectingColumns(toSdkStringList(model.getChangeDetectingColumns()))
                .fieldMappings(toSdk(model.getFieldMappings()))
                .build();
    }

    private static List<software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping> toSdk(
            List<DataSourceToIndexFieldMapping> model) {
        if (model == null) {
            return null;
        }
        return model.stream().map(x -> toSdk(x)).collect(Collectors.toList());
    }

    private static software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping toSdk(
            DataSourceToIndexFieldMapping model) {
        if (model == null ) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping
                .builder()
                .dataSourceFieldName(model.getDataSourceFieldName())
                .dateFieldFormat(model.getDateFieldFormat())
                .indexFieldName(model.getIndexFieldName())
                .build();
    }

    private static software.amazon.awssdk.services.kendra.model.AclConfiguration toSdk(AclConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.AclConfiguration
                .builder()
                .allowedGroupsColumnName(model.getAllowedGroupsColumnName())
                .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.DatabaseConfiguration sdk) {
        return software.amazon.kendra.datasource.DataSourceConfiguration
                .builder()
                .databaseConfiguration(toModel(sdk))
                .build();
    }

    public static DatabaseConfiguration toModel(software.amazon.awssdk.services.kendra.model.DatabaseConfiguration sdk) {
        return DatabaseConfiguration
                .builder()
                .databaseEngineType(sdk.databaseEngineTypeAsString())
                .connectionConfiguration(toModel(sdk.connectionConfiguration()))
                .vpcConfiguration(toModel(sdk.vpcConfiguration()))
                .columnConfiguration(toModel(sdk.columnConfiguration()))
                .aclConfiguration(toModel(sdk.aclConfiguration()))
                .build();
    }

    private static ConnectionConfiguration toModel(software.amazon.awssdk.services.kendra.model.ConnectionConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ConnectionConfiguration
                .builder()
                .databaseHost(sdk.databaseHost())
                .databasePort(sdk.databasePort())
                .databaseName(sdk.databaseName())
                .secretArn(sdk.secretArn())
                .tableName(sdk.tableName())
                .build();
    }

    private static DataSourceVpcConfiguration toModel(software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return DataSourceVpcConfiguration
                .builder()
                .securityGroupIds(toModelStringList(sdk.securityGroupIds()))
                .subnetIds(toModelStringList(sdk.subnetIds()))
                .build();
    }

    private static List<String> toModelStringList(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        return stringList.stream().collect(Collectors.toList());
    }

    private static ColumnConfiguration toModel(software.amazon.awssdk.services.kendra.model.ColumnConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ColumnConfiguration
                .builder()
                .documentIdColumnName(sdk.documentIdColumnName())
                .documentDataColumnName(sdk.documentDataColumnName())
                .documentTitleColumnName(sdk.documentTitleColumnName())
                .fieldMappings(toModel(sdk.fieldMappings()))
                .changeDetectingColumns(toModelStringList(sdk.changeDetectingColumns()))
                .build();
    }

    private static List<DataSourceToIndexFieldMapping> toModel(List<software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping> sdk) {
        if (sdk == null || sdk.isEmpty()) {
            return null;
        }
        return sdk.stream().map(x -> toModel(x)).collect(Collectors.toList());
    }

    private static DataSourceToIndexFieldMapping toModel(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping sdk) {
        if (sdk == null) {
            return null;
        }
        return DataSourceToIndexFieldMapping
                .builder()
                .dataSourceFieldName(sdk.dataSourceFieldName())
                .indexFieldName(sdk.indexFieldName())
                .dateFieldFormat(sdk.dateFieldFormat())
                .build();
    }

    private static AclConfiguration toModel(software.amazon.awssdk.services.kendra.model.AclConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return AclConfiguration
                .builder()
                .allowedGroupsColumnName(sdk.allowedGroupsColumnName())
                .build();
    }

}
