package software.amazon.kendra.datasource.convert.gdrive;

import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_MIME_TYPES;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_MIME_TYPES_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_MIME_TYPES_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_SHARED_DRIVES;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_SHARED_DRIVES_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_SHARED_DRIVES_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_USER_ACCOUNTS;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_USER_ACCOUNTS_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUDE_USER_ACCOUNTS_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUSION_PATTERNS;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUSION_PATTERNS_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.EXCLUSION_PATTERNS_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_MODEL;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_MODEL_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_MODEL_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_SDK;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_SDK_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.FIELD_MAPPINGS_SDK_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.INCLUSION_PATTERNS;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.INCLUSION_PATTERNS_EMPTY;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.INCLUSION_PATTERNS_SINGLE;
import static software.amazon.kendra.datasource.convert.gdrive.GoogleDriveConverterTestCaseConstants.SECRET_ARN;

import software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration;

/**
 * GoogleDriveConverterTestCases contains all test cases used by GoogleDriveConverterTest.
 * @see GoogleDriveConverterTestCase - how singular test cases are represented
 * @see GoogleDriveConverterTest - consumer of this class' data
 * @see GoogleDriveConverterTestCaseConstants - constants uses in this class
 */
public final class GoogleDriveConverterTestCases {

    static final GoogleDriveConverterTestCase[] TEST_CASES = {
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Null")
            .input(new GoogleDriveConverterTestCase.SdkData(null))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(null))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Null fields")
            .input(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .build()))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Full configuration, multi-value lists")
            .input(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS)
                    .exclusionPatterns(EXCLUSION_PATTERNS)
                    .fieldMappings(FIELD_MAPPINGS_SDK)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES)
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS)
                    .exclusionPatterns(EXCLUSION_PATTERNS)
                    .fieldMappings(FIELD_MAPPINGS_MODEL)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES)
                    .build()))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Full configuration, single-value lists")
            .input(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS_SINGLE)
                    .exclusionPatterns(EXCLUSION_PATTERNS_SINGLE)
                    .fieldMappings(FIELD_MAPPINGS_SDK_SINGLE)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES_SINGLE)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS_SINGLE)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES_SINGLE)
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS_SINGLE)
                    .exclusionPatterns(EXCLUSION_PATTERNS_SINGLE)
                    .fieldMappings(FIELD_MAPPINGS_MODEL_SINGLE)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES_SINGLE)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS_SINGLE)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES_SINGLE)
                    .build()))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Base configuration")
            .input(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .build()))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Empty sdk lists -> null model lists")
            .isSymmetrical(false)
            .input(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS_EMPTY)
                    .exclusionPatterns(EXCLUSION_PATTERNS_EMPTY)
                    .fieldMappings(FIELD_MAPPINGS_SDK_EMPTY)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES_EMPTY)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS_EMPTY)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES_EMPTY)
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .build()))
            .build(),
        //</editor-fold>
        //<editor-fold>
        GoogleDriveConverterTestCase.builder()
            .description("Empty model lists -> empty sdk lists")
            .isSymmetrical(false)
            .input(new GoogleDriveConverterTestCase.ModelData(
                software.amazon.kendra.datasource.GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS_EMPTY)
                    .exclusionPatterns(EXCLUSION_PATTERNS_EMPTY)
                    .fieldMappings(FIELD_MAPPINGS_MODEL_EMPTY)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES_EMPTY)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS_EMPTY)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES_EMPTY)
                    .build()))
            .expectedOutput(new GoogleDriveConverterTestCase.SdkData(
                GoogleDriveConfiguration.builder()
                    .secretArn(SECRET_ARN)
                    .inclusionPatterns(INCLUSION_PATTERNS_EMPTY)
                    .exclusionPatterns(EXCLUSION_PATTERNS_EMPTY)
                    .fieldMappings(FIELD_MAPPINGS_SDK_EMPTY)
                    .excludeMimeTypes(EXCLUDE_MIME_TYPES_EMPTY)
                    .excludeUserAccounts(EXCLUDE_USER_ACCOUNTS_EMPTY)
                    .excludeSharedDrives(EXCLUDE_SHARED_DRIVES_EMPTY)
                    .build()))
            .build(),
        //</editor-fold>
    };

    private GoogleDriveConverterTestCases() {
        // utility class
    }
}
