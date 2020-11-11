package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.AclConfiguration;
import software.amazon.kendra.datasource.ColumnConfiguration;
import software.amazon.kendra.datasource.ConnectionConfiguration;
import software.amazon.kendra.datasource.DataSourceConfiguration;
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

    public static software.amazon.awssdk.services.kendra.model.DatabaseConfiguration toSdk(DatabaseConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.DatabaseConfiguration
                .builder()
                .databaseEngineType(model.getDatabaseEngineType())
                .connectionConfiguration(toSdk(model.getConnectionConfiguration()))
                .vpcConfiguration(DataSourceVpcConfigurationConverter.toSdk(model.getVpcConfiguration()))
                .columnConfiguration(toSdk(model.getColumnConfiguration()))
                .aclConfiguration(toSdk(model.getAclConfiguration()))
                .sqlConfiguration(toSdk(model.getSqlConfiguration()))
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
                .fieldMappings(ListConverter.toSdk(model.getFieldMappings(), FieldMappingConverter::toSdk))
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

    private static software.amazon.awssdk.services.kendra.model.SqlConfiguration toSdk(SqlConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.SqlConfiguration
                .builder()
                .queryIdentifiersEnclosingOption(model.getQueryIdentifiersEnclosingOption())
                .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(software.amazon.awssdk.services.kendra.model.DatabaseConfiguration sdk) {
        return software.amazon.kendra.datasource.DataSourceConfiguration
                .builder()
                .databaseConfiguration(toModel(sdk))
                .build();
    }

    public static DatabaseConfiguration toModel(software.amazon.awssdk.services.kendra.model.DatabaseConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return DatabaseConfiguration
                .builder()
                .databaseEngineType(sdk.databaseEngineTypeAsString())
                .connectionConfiguration(toModel(sdk.connectionConfiguration()))
                .vpcConfiguration(DataSourceVpcConfigurationConverter.toModel(sdk.vpcConfiguration()))
                .columnConfiguration(toModel(sdk.columnConfiguration()))
                .aclConfiguration(toModel(sdk.aclConfiguration()))
                .sqlConfiguration(toModel(sdk.sqlConfiguration()))
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

    private static ColumnConfiguration toModel(software.amazon.awssdk.services.kendra.model.ColumnConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return ColumnConfiguration
                .builder()
                .documentIdColumnName(sdk.documentIdColumnName())
                .documentDataColumnName(sdk.documentDataColumnName())
                .documentTitleColumnName(sdk.documentTitleColumnName())
                .fieldMappings(ListConverter.toSdk(sdk.fieldMappings(), FieldMappingConverter::toModel))
                .changeDetectingColumns(StringListConverter.toModel(sdk.changeDetectingColumns()))
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

    private static SqlConfiguration toModel(software.amazon.awssdk.services.kendra.model.SqlConfiguration sdk) {
       if (sdk == null) {
          return null;
       }
        return SqlConfiguration
                .builder()
                .queryIdentifiersEnclosingOption(sdk.queryIdentifiersEnclosingOptionAsString())
                .build();
    }

}
