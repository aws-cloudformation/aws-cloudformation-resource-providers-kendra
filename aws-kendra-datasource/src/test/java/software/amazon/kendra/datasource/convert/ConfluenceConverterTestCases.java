package software.amazon.kendra.datasource.convert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentConfiguration;
import software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentFieldName;
import software.amazon.awssdk.services.kendra.model.ConfluenceAttachmentToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.ConfluenceBlogConfiguration;
import software.amazon.awssdk.services.kendra.model.ConfluenceBlogFieldName;
import software.amazon.awssdk.services.kendra.model.ConfluenceBlogToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration;
import software.amazon.awssdk.services.kendra.model.ConfluencePageConfiguration;
import software.amazon.awssdk.services.kendra.model.ConfluencePageFieldName;
import software.amazon.awssdk.services.kendra.model.ConfluencePageToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.ConfluenceSpaceConfiguration;
import software.amazon.awssdk.services.kendra.model.ConfluenceSpaceFieldName;
import software.amazon.awssdk.services.kendra.model.ConfluenceSpaceToIndexFieldMapping;
import software.amazon.awssdk.services.kendra.model.ConfluenceVersion;
import software.amazon.awssdk.services.kendra.model.DataSourceVpcConfiguration;

public final class ConfluenceConverterTestCases {

    private static final String SERVER_URL = "SERVER_URL";
    private static final String SECRET_ARN = "SECRET_ARN";
    private static final String DATE_FIELD_FORMAT = "DATE_FIELD_FORMAT";
    private static final List<String> INCLUSION_PATTERNS = Arrays.asList("INC_PAT1", "INC_PAT2");
    private static final List<String> INCLUSION_PATTERNS_WITH_NULL = Arrays.asList("INC_PAT1", null, "INC_PAT2");
    private static final List<String> EXCLUSION_PATTERNS = Arrays.asList("EXC_PAT1", "EXC_PAT2");
    private static final List<String> EXCLUSION_PATTERNS_WITH_NULL = Arrays.asList("EXC_PAT1", null, "EXC_PAT2");
    private static final List<String> INCLUDE_SPACES = Arrays.asList("INC_SPC1", "INC_SPC2");
    private static final List<String> INCLUDE_SPACES_WITH_NULL = Arrays.asList("INC_SPC1", null, "INC_SPC2");
    private static final List<String> EXCLUDE_SPACES = Arrays.asList("EXC_SPC1", "EXC_SPC2");
    private static final List<String> EXCLUDE_SPACES_WITH_NULL = Arrays.asList("EXC_SPC1", null, "EXC_SPC2");
    private static final List<String> SUBNET_IDS = Arrays.asList("subnet-1", "subnet-2");
    private static final List<String> SUBNET_IDS_WITH_NULL = Arrays.asList("subnet-1", null, "subnet-2");
    private static final List<String> SECURITY_GROUP_IDS = Arrays.asList("sg-1", "sg-2");
    private static final List<String> SECURITY_GROUP_IDS_WITH_NULL = Arrays.asList("sg-1", null, "sg-2");

    static final ConfluenceConverterTestCase[] TEST_CASES = {
        ConfluenceConverterTestCase.builder()
            .description("Null configuration")
            .reflexive(true)
            .input(new ConfluenceConverterTestCase.Sdk(null))
            .output(new ConfluenceConverterTestCase.Model(null))
            .build(),
        ConfluenceConverterTestCase.builder()
            .description("All fields null")
            .reflexive(true)
            .input(new ConfluenceConverterTestCase.Sdk(
                ConfluenceConfiguration.builder().build()
            ))
            .output(new ConfluenceConverterTestCase.Model(
                software.amazon.kendra.datasource.ConfluenceConfiguration.builder().build()
            ))
            .build(),
        ConfluenceConverterTestCase.builder()
            .description("Bare minimum required configuration")
            .reflexive(true)
            .input(new ConfluenceConverterTestCase.Sdk(
                software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration.builder()
                    .serverUrl(SERVER_URL)
                    .secretArn(SECRET_ARN)
                    .version(ConfluenceVersion.CLOUD)
                    .build()
            ))
            .output(new ConfluenceConverterTestCase.Model(
                software.amazon.kendra.datasource.ConfluenceConfiguration.builder()
                    .serverUrl(SERVER_URL)
                    .secretArn(SECRET_ARN)
                    .version(ConfluenceVersion.CLOUD.toString())
                    .build()
            ))
            .build(),
        ConfluenceConverterTestCase.builder()
            .description("Full Configuration")
            .reflexive(true)
            .input(new ConfluenceConverterTestCase.Sdk(
                software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration.builder()
                    .serverUrl(SERVER_URL)
                    .secretArn(SECRET_ARN)
                    .version(ConfluenceVersion.SERVER)
                    .vpcConfiguration(DataSourceVpcConfiguration.builder()
                        .subnetIds(SUBNET_IDS)
                        .securityGroupIds(SECURITY_GROUP_IDS)
                        .build())
                    .spaceConfiguration(ConfluenceSpaceConfiguration.builder()
                        .includeSpaces(INCLUDE_SPACES)
                        .excludeSpaces(EXCLUDE_SPACES)
                        .crawlPersonalSpaces(true)
                        .crawlArchivedSpaces(false)
                        .spaceFieldMappings(Arrays.asList(
                            ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.DISPLAY_URL)
                                .indexFieldName(ConfluenceSpaceFieldName.DISPLAY_URL.toString())
                                .build(),
                            ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.ITEM_TYPE)
                                .indexFieldName(ConfluenceSpaceFieldName.ITEM_TYPE.toString())
                                .build(),
                            ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.SPACE_KEY)
                                .indexFieldName(ConfluenceSpaceFieldName.SPACE_KEY.toString())
                                .build(),
                            ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.URL)
                                .indexFieldName(ConfluenceSpaceFieldName.URL.toString())
                                .build()
                        ))
                        .build())
                    .pageConfiguration(ConfluencePageConfiguration.builder()
                        .pageFieldMappings(Arrays.asList(
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.AUTHOR)
                                .indexFieldName(ConfluencePageFieldName.AUTHOR.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.CONTENT_STATUS)
                                .indexFieldName(ConfluencePageFieldName.CONTENT_STATUS.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.CREATED_DATE)
                                .indexFieldName(ConfluencePageFieldName.CREATED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.DISPLAY_URL)
                                .indexFieldName(ConfluencePageFieldName.DISPLAY_URL.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.ITEM_TYPE)
                                .indexFieldName(ConfluencePageFieldName.ITEM_TYPE.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.LABELS)
                                .indexFieldName(ConfluencePageFieldName.LABELS.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.MODIFIED_DATE)
                                .indexFieldName(ConfluencePageFieldName.MODIFIED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.PARENT_ID)
                                .indexFieldName(ConfluencePageFieldName.PARENT_ID.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.SPACE_NAME)
                                .indexFieldName(ConfluencePageFieldName.SPACE_NAME.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.SPACE_KEY)
                                .indexFieldName(ConfluencePageFieldName.SPACE_KEY.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.URL)
                                .indexFieldName(ConfluencePageFieldName.URL.toString())
                                .build(),
                            ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.VERSION)
                                .indexFieldName(ConfluencePageFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .blogConfiguration(ConfluenceBlogConfiguration.builder()
                        .blogFieldMappings(Arrays.asList(
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.AUTHOR)
                                .indexFieldName(ConfluenceBlogFieldName.AUTHOR.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.DISPLAY_URL)
                                .indexFieldName(ConfluenceBlogFieldName.DISPLAY_URL.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.ITEM_TYPE)
                                .indexFieldName(ConfluenceBlogFieldName.ITEM_TYPE.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.LABELS)
                                .indexFieldName(ConfluenceBlogFieldName.LABELS.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.PUBLISH_DATE)
                                .indexFieldName(ConfluenceBlogFieldName.PUBLISH_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.SPACE_NAME)
                                .indexFieldName(ConfluenceBlogFieldName.SPACE_NAME.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.SPACE_KEY)
                                .indexFieldName(ConfluenceBlogFieldName.SPACE_KEY.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.URL)
                                .indexFieldName(ConfluenceBlogFieldName.URL.toString())
                                .build(),
                            ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.VERSION)
                                .indexFieldName(ConfluenceBlogFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .attachmentConfiguration(ConfluenceAttachmentConfiguration.builder()
                        .crawlAttachments(true)
                        .attachmentFieldMappings(Arrays.asList(
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.AUTHOR)
                                .indexFieldName(ConfluenceAttachmentFieldName.AUTHOR.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.CONTENT_TYPE)
                                .indexFieldName(ConfluenceAttachmentFieldName.CONTENT_TYPE.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.CREATED_DATE)
                                .indexFieldName(ConfluenceAttachmentFieldName.CREATED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.DISPLAY_URL)
                                .indexFieldName(ConfluenceAttachmentFieldName.DISPLAY_URL.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.FILE_SIZE)
                                .indexFieldName(ConfluenceAttachmentFieldName.FILE_SIZE.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.ITEM_TYPE)
                                .indexFieldName(ConfluenceAttachmentFieldName.ITEM_TYPE.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.PARENT_ID)
                                .indexFieldName(ConfluenceAttachmentFieldName.PARENT_ID.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.SPACE_NAME)
                                .indexFieldName(ConfluenceAttachmentFieldName.SPACE_NAME.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.SPACE_KEY)
                                .indexFieldName(ConfluenceAttachmentFieldName.SPACE_KEY.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.URL)
                                .indexFieldName(ConfluenceAttachmentFieldName.URL.toString())
                                .build(),
                            ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.VERSION)
                                .indexFieldName(ConfluenceAttachmentFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .inclusionPatterns(INCLUSION_PATTERNS)
                    .exclusionPatterns(EXCLUSION_PATTERNS)
                    .build()
            ))
            .output(new ConfluenceConverterTestCase.Model(
                software.amazon.kendra.datasource.ConfluenceConfiguration.builder()
                    .serverUrl(SERVER_URL)
                    .secretArn(SECRET_ARN)
                    .version(ConfluenceVersion.SERVER.toString())
                    .vpcConfiguration(software.amazon.kendra.datasource.DataSourceVpcConfiguration.builder()
                        .subnetIds(SUBNET_IDS)
                        .securityGroupIds(SECURITY_GROUP_IDS)
                        .build())
                    .spaceConfiguration(software.amazon.kendra.datasource.ConfluenceSpaceConfiguration.builder()
                        .includeSpaces(INCLUDE_SPACES)
                        .excludeSpaces(EXCLUDE_SPACES)
                        .crawlPersonalSpaces(true)
                        .crawlArchivedSpaces(false)
                        .spaceFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.DISPLAY_URL.toString())
                                .indexFieldName(ConfluenceSpaceFieldName.DISPLAY_URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.ITEM_TYPE.toString())
                                .indexFieldName(ConfluenceSpaceFieldName.ITEM_TYPE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.SPACE_KEY.toString())
                                .indexFieldName(ConfluenceSpaceFieldName.SPACE_KEY.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceSpaceFieldName.URL.toString())
                                .indexFieldName(ConfluenceSpaceFieldName.URL.toString())
                                .build()
                        ))
                        .build())
                    .pageConfiguration(software.amazon.kendra.datasource.ConfluencePageConfiguration.builder()
                        .pageFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.AUTHOR.toString())
                                .indexFieldName(ConfluencePageFieldName.AUTHOR.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.CONTENT_STATUS.toString())
                                .indexFieldName(ConfluencePageFieldName.CONTENT_STATUS.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.CREATED_DATE.toString())
                                .indexFieldName(ConfluencePageFieldName.CREATED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.DISPLAY_URL.toString())
                                .indexFieldName(ConfluencePageFieldName.DISPLAY_URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.ITEM_TYPE.toString())
                                .indexFieldName(ConfluencePageFieldName.ITEM_TYPE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.LABELS.toString())
                                .indexFieldName(ConfluencePageFieldName.LABELS.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.MODIFIED_DATE.toString())
                                .indexFieldName(ConfluencePageFieldName.MODIFIED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.PARENT_ID.toString())
                                .indexFieldName(ConfluencePageFieldName.PARENT_ID.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.SPACE_NAME.toString())
                                .indexFieldName(ConfluencePageFieldName.SPACE_NAME.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.SPACE_KEY.toString())
                                .indexFieldName(ConfluencePageFieldName.SPACE_KEY.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.URL.toString())
                                .indexFieldName(ConfluencePageFieldName.URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluencePageFieldName.VERSION.toString())
                                .indexFieldName(ConfluencePageFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .blogConfiguration(software.amazon.kendra.datasource.ConfluenceBlogConfiguration.builder()
                        .blogFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.AUTHOR.toString())
                                .indexFieldName(ConfluenceBlogFieldName.AUTHOR.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.DISPLAY_URL.toString())
                                .indexFieldName(ConfluenceBlogFieldName.DISPLAY_URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.ITEM_TYPE.toString())
                                .indexFieldName(ConfluenceBlogFieldName.ITEM_TYPE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.LABELS.toString())
                                .indexFieldName(ConfluenceBlogFieldName.LABELS.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.PUBLISH_DATE.toString())
                                .indexFieldName(ConfluenceBlogFieldName.PUBLISH_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.SPACE_NAME.toString())
                                .indexFieldName(ConfluenceBlogFieldName.SPACE_NAME.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.SPACE_KEY.toString())
                                .indexFieldName(ConfluenceBlogFieldName.SPACE_KEY.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.URL.toString())
                                .indexFieldName(ConfluenceBlogFieldName.URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceBlogFieldName.VERSION.toString())
                                .indexFieldName(ConfluenceBlogFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .attachmentConfiguration(software.amazon.kendra.datasource.ConfluenceAttachmentConfiguration.builder()
                        .crawlAttachments(true)
                        .attachmentFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.AUTHOR.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.AUTHOR.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.CONTENT_TYPE.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.CONTENT_TYPE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.CREATED_DATE.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.CREATED_DATE.toString())
                                .dateFieldFormat(DATE_FIELD_FORMAT)
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.DISPLAY_URL.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.DISPLAY_URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.FILE_SIZE.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.FILE_SIZE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.ITEM_TYPE.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.ITEM_TYPE.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.PARENT_ID.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.PARENT_ID.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.SPACE_NAME.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.SPACE_NAME.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.SPACE_KEY.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.SPACE_KEY.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.URL.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.URL.toString())
                                .build(),
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder()
                                .dataSourceFieldName(ConfluenceAttachmentFieldName.VERSION.toString())
                                .indexFieldName(ConfluenceAttachmentFieldName.VERSION.toString())
                                .build()
                        ))
                        .build())
                    .inclusionPatterns(INCLUSION_PATTERNS)
                    .exclusionPatterns(EXCLUSION_PATTERNS)
                    .build()
            ))
            .build(),
        ConfluenceConverterTestCase.builder()
            .description("Empty lists in sdk are converted to nulls in model")
            .reflexive(false)
            .input(new ConfluenceConverterTestCase.Sdk(
                ConfluenceConfiguration.builder()
                    .vpcConfiguration(DataSourceVpcConfiguration.builder()
                        .subnetIds(Collections.emptyList())
                        .securityGroupIds(Collections.emptyList())
                        .build())
                    .spaceConfiguration(ConfluenceSpaceConfiguration.builder()
                        .includeSpaces(Collections.emptyList())
                        .excludeSpaces(Collections.emptyList())
                        .spaceFieldMappings(Collections.emptyList())
                        .build())
                    .pageConfiguration(ConfluencePageConfiguration.builder()
                        .pageFieldMappings(Collections.emptyList())
                        .build())
                    .blogConfiguration(ConfluenceBlogConfiguration.builder()
                        .blogFieldMappings(Collections.emptyList())
                        .build())
                    .attachmentConfiguration(ConfluenceAttachmentConfiguration.builder()
                        .attachmentFieldMappings(Collections.emptyList())
                        .build())
                    .inclusionPatterns(Collections.emptyList())
                    .exclusionPatterns(Collections.emptyList())
                    .build()
            ))
            .output(new ConfluenceConverterTestCase.Model(
                software.amazon.kendra.datasource.ConfluenceConfiguration.builder()
                    .vpcConfiguration(software.amazon.kendra.datasource.DataSourceVpcConfiguration.builder().build())
                    .spaceConfiguration(software.amazon.kendra.datasource.ConfluenceSpaceConfiguration.builder().build())
                    .pageConfiguration(software.amazon.kendra.datasource.ConfluencePageConfiguration.builder().build())
                    .blogConfiguration(software.amazon.kendra.datasource.ConfluenceBlogConfiguration.builder().build())
                    .attachmentConfiguration(software.amazon.kendra.datasource.ConfluenceAttachmentConfiguration.builder().build())
                    .build()
            ))
            .build(),
        ConfluenceConverterTestCase.builder()
            .description("Null handling of list elements")
            .reflexive(true)
            .input(new ConfluenceConverterTestCase.Model(
                software.amazon.kendra.datasource.ConfluenceConfiguration.builder()
                    .vpcConfiguration(software.amazon.kendra.datasource.DataSourceVpcConfiguration.builder()
                        .securityGroupIds(SECURITY_GROUP_IDS_WITH_NULL)
                        .subnetIds(SUBNET_IDS_WITH_NULL)
                        .build())
                    .spaceConfiguration(software.amazon.kendra.datasource.ConfluenceSpaceConfiguration.builder()
                        .includeSpaces(INCLUDE_SPACES_WITH_NULL)
                        .excludeSpaces(EXCLUDE_SPACES_WITH_NULL)
                        .spaceFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceSpaceToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .pageConfiguration(software.amazon.kendra.datasource.ConfluencePageConfiguration.builder()
                        .pageFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluencePageToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .blogConfiguration(software.amazon.kendra.datasource.ConfluenceBlogConfiguration.builder()
                        .blogFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceBlogToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .attachmentConfiguration(software.amazon.kendra.datasource.ConfluenceAttachmentConfiguration.builder()
                        .attachmentFieldMappings(Arrays.asList(
                            software.amazon.kendra.datasource.ConfluenceAttachmentToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .inclusionPatterns(INCLUSION_PATTERNS_WITH_NULL)
                    .exclusionPatterns(EXCLUSION_PATTERNS_WITH_NULL)
                    .build()
            ))
            .output(new ConfluenceConverterTestCase.Sdk(
                ConfluenceConfiguration.builder()
                    .vpcConfiguration(DataSourceVpcConfiguration.builder()
                        .securityGroupIds(SECURITY_GROUP_IDS_WITH_NULL)
                        .subnetIds(SUBNET_IDS_WITH_NULL)
                        .build())
                    .spaceConfiguration(ConfluenceSpaceConfiguration.builder()
                        .includeSpaces(INCLUDE_SPACES_WITH_NULL)
                        .excludeSpaces(EXCLUDE_SPACES_WITH_NULL)
                        .spaceFieldMappings(Arrays.asList(
                            ConfluenceSpaceToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .pageConfiguration(ConfluencePageConfiguration.builder()
                        .pageFieldMappings(Arrays.asList(
                            ConfluencePageToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .blogConfiguration(ConfluenceBlogConfiguration.builder()
                        .blogFieldMappings(Arrays.asList(
                            ConfluenceBlogToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .attachmentConfiguration(ConfluenceAttachmentConfiguration.builder()
                        .attachmentFieldMappings(Arrays.asList(
                            ConfluenceAttachmentToIndexFieldMapping.builder().build(),
                            null
                        ))
                        .build())
                    .inclusionPatterns(INCLUSION_PATTERNS_WITH_NULL)
                    .exclusionPatterns(EXCLUSION_PATTERNS_WITH_NULL)
                    .build()
            ))
            .build()
    };

    private ConfluenceConverterTestCases() {
        // utility class
    }
}
