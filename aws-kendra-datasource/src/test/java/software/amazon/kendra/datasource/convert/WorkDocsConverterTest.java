package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.DataSourceToIndexFieldMapping;
import software.amazon.kendra.datasource.WorkDocsConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkDocsConverterTest {
    private static final String ORG_ID = "d-1234567890";
    private static final List<String> INCLUSION_LIST = Arrays.asList("IN1", "IN2");
    private static final List<String> EXCLUSION_LIST = Arrays.asList("EX1", "EX2");
    private static final String FIELD_MAPPING_DATA_SOURCE_FIELD_NAME = "testDataSourceFieldName";
    private static final String FIELD_MAPPING_DATE_FIELD_FORMAT = "testDateFieldFormat";
    private static final String FIELD_MAPPING_INDEX_FIELD_NAME = "testIndexFieldName";

    @Test
    public void testSdkDataSourceConfiguration_Null() {
        assertThat(WorkDocsConverter.toSdkDataSourceConfiguration(null))
            .isEqualTo(null);
    }

    @Test
    public void testModelDataSourceConfiguration_Null() {
        assertThat(WorkDocsConverter.toModelDataSourceConfiguration(null)
            .getWorkDocsConfiguration()).isEqualTo(null);
    }

    @Test
    public void testSdkDataSourceConfiguration_EmptyConfig() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .build())
            .build();

        software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                .build();

        assertThat(WorkDocsConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getWorkDocsConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testModelDataSourceConfiguration_EmptyConfig() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                    .build())
                .build();

        DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .build())
            .build();

        assertThat(WorkDocsConverter.toModelDataSourceConfiguration(dataSourceConfiguration.workDocsConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testSdkDataSourceConfiguration_MinimalConfig() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .build())
            .build();

        software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .build();

        assertThat(WorkDocsConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getWorkDocsConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testModelDataSourceConfiguration_MinimalConfig() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                    .organizationId(ORG_ID)
                    .build())
                .build();

        DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .build())
            .build();

        assertThat(WorkDocsConverter.toModelDataSourceConfiguration(dataSourceConfiguration.workDocsConfiguration()))
            .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testSdkDataSourceConfiguration_FullConfig() {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .crawlComments(true)
                .useChangeLog(false)
                .inclusionPatterns(INCLUSION_LIST)
                .exclusionPatterns(EXCLUSION_LIST)
                .fieldMappings(Collections.singletonList(DataSourceToIndexFieldMapping.builder()
                    .dataSourceFieldName(FIELD_MAPPING_DATA_SOURCE_FIELD_NAME)
                    .dateFieldFormat(FIELD_MAPPING_DATE_FIELD_FORMAT)
                    .indexFieldName(FIELD_MAPPING_INDEX_FIELD_NAME)
                    .build()))
                .build())
            .build();

        software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration expectedDataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                    .crawlComments(true)
                    .useChangeLog(false)
                    .inclusionPatterns(INCLUSION_LIST)
                    .exclusionPatterns(EXCLUSION_LIST)
                    .fieldMappings(Collections.singletonList(
                        software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName(FIELD_MAPPING_DATA_SOURCE_FIELD_NAME)
                            .dateFieldFormat(FIELD_MAPPING_DATE_FIELD_FORMAT)
                            .indexFieldName(FIELD_MAPPING_INDEX_FIELD_NAME)
                            .build()))
                .build();

        assertThat(WorkDocsConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getWorkDocsConfiguration()))
                .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testModelDataSourceConfiguration_FullConfig() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                    .organizationId(ORG_ID)
                    .crawlComments(true)
                    .useChangeLog(false)
                    .inclusionPatterns(INCLUSION_LIST)
                    .exclusionPatterns(EXCLUSION_LIST)
                    .fieldMappings(Collections.singletonList(
                        software.amazon.awssdk.services.kendra.model.DataSourceToIndexFieldMapping.builder()
                            .dataSourceFieldName(FIELD_MAPPING_DATA_SOURCE_FIELD_NAME)
                            .dateFieldFormat(FIELD_MAPPING_DATE_FIELD_FORMAT)
                            .indexFieldName(FIELD_MAPPING_INDEX_FIELD_NAME)
                            .build()))
                    .build())
                .build();

        DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .crawlComments(true)
                .useChangeLog(false)
                .inclusionPatterns(INCLUSION_LIST)
                .exclusionPatterns(EXCLUSION_LIST)
                .fieldMappings(Collections.singletonList(DataSourceToIndexFieldMapping.builder()
                    .dataSourceFieldName(FIELD_MAPPING_DATA_SOURCE_FIELD_NAME)
                    .dateFieldFormat(FIELD_MAPPING_DATE_FIELD_FORMAT)
                    .indexFieldName(FIELD_MAPPING_INDEX_FIELD_NAME)
                    .build()))
                .build())
            .build();

        assertThat(WorkDocsConverter.toModelDataSourceConfiguration(dataSourceConfiguration.workDocsConfiguration()))
                .isEqualTo(expectedDataSourceConfiguration);
    }

    @Test
    public void testModelDataSourceConfiguration_EmptyList() {
        software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration =
            software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder()
                .workDocsConfiguration(software.amazon.awssdk.services.kendra.model.WorkDocsConfiguration.builder()
                    .organizationId(ORG_ID)
                    .crawlComments(true)
                    .useChangeLog(false)
                    .inclusionPatterns(Collections.emptyList())
                    .exclusionPatterns(Collections.emptyList())
                    .fieldMappings(Collections.emptyList())
                    .build())
                .build();

        DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
            .workDocsConfiguration(WorkDocsConfiguration.builder()
                .organizationId(ORG_ID)
                .crawlComments(true)
                .useChangeLog(false)
                .build())
            .build();

        assertThat(WorkDocsConverter.toModelDataSourceConfiguration(dataSourceConfiguration.workDocsConfiguration()))
                .isEqualTo(expectedDataSourceConfiguration);
    }
}
