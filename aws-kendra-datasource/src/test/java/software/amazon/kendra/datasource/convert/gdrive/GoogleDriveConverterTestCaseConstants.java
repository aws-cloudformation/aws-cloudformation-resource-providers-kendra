package software.amazon.kendra.datasource.convert.gdrive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping;

/**
 * GoogleDriveConverterTestCaseConstants holds constant data used for test cases.
 * @see GoogleDriveConverterTestCases - where this data is consumed
 */
public final class GoogleDriveConverterTestCaseConstants {

    static final String SECRET_ARN = "SECRET_ARN";

    static final List<String> INCLUSION_PATTERNS = Arrays.asList("INC1", "INC2", "INC3");
    static final List<String> INCLUSION_PATTERNS_SINGLE = Collections.singletonList("INC1");
    static final List<String> INCLUSION_PATTERNS_EMPTY = Collections.emptyList();

    static final List<String> EXCLUSION_PATTERNS = Arrays.asList("EXC1", "EXC2", "EXC3");
    static final List<String> EXCLUSION_PATTERNS_SINGLE = Collections.singletonList("EXC1");
    static final List<String> EXCLUSION_PATTERNS_EMPTY = Collections.emptyList();

    static final List<DataSourceToIndexFieldMapping> FIELD_MAPPINGS_SDK = Arrays.asList(
        DataSourceToIndexFieldMapping.builder().dataSourceFieldName("DSFN1").indexFieldName("IFN1").dateFieldFormat("DFF1").build(),
        DataSourceToIndexFieldMapping.builder().dataSourceFieldName("DSFN2").indexFieldName("IFN2").dateFieldFormat("DFF2").build(),
        DataSourceToIndexFieldMapping.builder().dataSourceFieldName("DSFN3").indexFieldName("IFN3").dateFieldFormat("DFF3").build()
    );

    static final List<DataSourceToIndexFieldMapping> FIELD_MAPPINGS_SDK_SINGLE = Collections.singletonList(
        DataSourceToIndexFieldMapping.builder().dataSourceFieldName("DSFN1").indexFieldName("IFN1").dateFieldFormat("DFF1").build()
    );

    static final List<DataSourceToIndexFieldMapping> FIELD_MAPPINGS_SDK_EMPTY = Collections.emptyList();

    static final List<software.amazon.kendra.datasource.DataSourceToIndexFieldMapping> FIELD_MAPPINGS_MODEL = Arrays.asList(
        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping.builder()
            .dataSourceFieldName("DSFN1").indexFieldName("IFN1").dateFieldFormat("DFF1").build(),
        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping.builder()
            .dataSourceFieldName("DSFN2").indexFieldName("IFN2").dateFieldFormat("DFF2").build(),
        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping.builder()
            .dataSourceFieldName("DSFN3").indexFieldName("IFN3").dateFieldFormat("DFF3").build()
    );

    static final List<software.amazon.kendra.datasource.DataSourceToIndexFieldMapping> FIELD_MAPPINGS_MODEL_SINGLE = Collections.singletonList(
        software.amazon.kendra.datasource.DataSourceToIndexFieldMapping.builder()
            .dataSourceFieldName("DSFN1").indexFieldName("IFN1").dateFieldFormat("DFF1").build()
    );

    static final List<software.amazon.kendra.datasource.DataSourceToIndexFieldMapping> FIELD_MAPPINGS_MODEL_EMPTY = Collections.emptyList();

    static final List<String> EXCLUDE_MIME_TYPES = Arrays.asList("EXC_MT1", "EXC_MT2", "EXC_MT3");
    static final List<String> EXCLUDE_MIME_TYPES_SINGLE = Collections.singletonList("EXC_MT1");
    static final List<String> EXCLUDE_MIME_TYPES_EMPTY = Collections.emptyList();

    static final List<String> EXCLUDE_USER_ACCOUNTS = Arrays.asList("EXC_UA1", "EXC_UA2", "EXC_UA3");
    static final List<String> EXCLUDE_USER_ACCOUNTS_SINGLE = Collections.singletonList("EXC_UA1");
    static final List<String> EXCLUDE_USER_ACCOUNTS_EMPTY = Collections.emptyList();

    static final List<String> EXCLUDE_SHARED_DRIVES = Arrays.asList("EXC_SD1", "EXC_SD2", "EXC_SD3");
    static final List<String> EXCLUDE_SHARED_DRIVES_SINGLE = Collections.singletonList("EXC_SD1");
    static final List<String> EXCLUDE_SHARED_DRIVES_EMPTY = Collections.emptyList();

    private GoogleDriveConverterTestCaseConstants() {
        // utility class
    }
}
