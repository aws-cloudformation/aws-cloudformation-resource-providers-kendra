package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.DataSourceToIndexFieldMapping;

import java.util.List;
import java.util.stream.Collectors;

public class FieldMappingConverter {

    public static software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping toSdk(DataSourceToIndexFieldMapping model) {
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

    public static DataSourceToIndexFieldMapping toModel(software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping sdk) {
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
}
