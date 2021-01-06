package software.amazon.kendra.datasource.convert.gdrive;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.convert.GoogleDriveConverter;

/**
 * GoogleDriveConverterTest tests that GoogleDriveConverter properly converts between AWS SDK and RPK models.
 * @see GoogleDriveConverter - the unit under test
 * @see GoogleDriveConverterTestCases - test cases for this class
 */
public class GoogleDriveConverterTest {

    /**
     * Given an SDK input, assert the converter produces the proper RPK output.
     */
    @ParameterizedTest
    @MethodSource("provideModelToSdkTests")
    public void testModelToSdk(GoogleDriveConverterTestCase testCase) {
        Assertions.assertThat(GoogleDriveConverter.toSdkDataSourceConfiguration(testCase.getInput().asModel()))
            .as(testCase.getDescription())
            .isEqualTo(testCase.getExpectedOutput().asSdk());
    }

    /**
     * Given an RPK input, assert the converter produces the proper SDK output.
     */
    @ParameterizedTest
    @MethodSource("provideSdkToModelTests")
    public void testSdkToModel(GoogleDriveConverterTestCase testCase) {
        assertThat(GoogleDriveConverter.toModelDataSourceConfiguration(testCase.getInput().asSdk()))
            .as(testCase.getDescription())
            .isEqualTo(DataSourceConfiguration.builder()
                .googleDriveConfiguration(testCase.getExpectedOutput().asModel())
                .build());
    }

    /**
     * @return All (model -> sdk) tests.
     */
    private static Stream<GoogleDriveConverterTestCase> provideModelToSdkTests() {
        return provideTestCases(false);
    }

    /**
     * @return All (sdk -> model) tests.
     */
    private static Stream<GoogleDriveConverterTestCase> provideSdkToModelTests() {
        return provideTestCases(true);
    }

    /**
     * Utility method used to generate a list of test cases for either (sdk -> model) or (model -> sdk).
     * @param sdkInput - If this is true then the "mode" of this method is (sdk -> model) otherwise (model -> sdk)
     * @return All test cases for the desired input mode.
     */
    private static Stream<GoogleDriveConverterTestCase> provideTestCases(boolean sdkInput) {
        return Arrays.stream(GoogleDriveConverterTestCases.TEST_CASES)
            // either the test case has the proper input, or it does not but is symmetrical
            .filter(testCase -> testCase.getInput().isSdk() == sdkInput || testCase.isSymmetrical())
            // if the test case has the proper input then it's a straightforward test, otherwise test in the opposite direction
            .map(testCase ->  testCase.getInput().isSdk() == sdkInput ? testCase : testCase.symmetricalTestCase());
    }
}
