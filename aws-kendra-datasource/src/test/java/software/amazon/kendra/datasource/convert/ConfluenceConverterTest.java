package software.amazon.kendra.datasource.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.convert.confluence.ConfluenceConverter;

public class ConfluenceConverterTest {

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testConversionWorksAsExpected(ConfluenceConverterTestCase testCase) {

        if (testCase.getInput() instanceof ConfluenceConverterTestCase.Model) {
            // model -> sdk
            assertThat(ConfluenceConverter.toSdkDataSourceConfiguration(testCase.getInput().asModel()))
                .as(String.format("model -> sdk : %s", testCase.getDescription()))
                .isEqualTo(testCase.getOutput().asSdk());

        } else {
            // sdk -> model
            DataSourceConfiguration actualOutput = ConfluenceConverter.toModelDataSourceConfiguration(testCase.getInput().asSdk());

            assertThat(actualOutput).isNotNull();

            assertThat(actualOutput.getS3Configuration()).isNull();
            assertThat(actualOutput.getSharePointConfiguration()).isNull();
            assertThat(actualOutput.getDatabaseConfiguration()).isNull();
            assertThat(actualOutput.getOneDriveConfiguration()).isNull();
            assertThat(actualOutput.getSalesforceConfiguration()).isNull();
            assertThat(actualOutput.getServiceNowConfiguration()).isNull();

            assertThat(actualOutput.getConfluenceConfiguration())
                .as(String.format("sdk -> model : %s", testCase.getDescription()))
                .isEqualTo(testCase.getOutput().asModel());
        }
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream
            .of(ConfluenceConverterTestCases.TEST_CASES)
            .flatMap(testCase -> {

                if (!testCase.getReflexive()) {
                    return Stream.of(testCase);
                }

                // same thing but in the opposite direction
                ConfluenceConverterTestCase reflexiveTestCase = ConfluenceConverterTestCase.builder()
                    .description(testCase.getDescription())
                    .reflexive(true)
                    .input(testCase.getOutput())
                    .output(testCase.getInput())
                    .build();

                return Stream.of(testCase, reflexiveTestCase);
            })
            .peek(ConfluenceConverterTest::validateTestCase)
            .map(Arguments::of);
    }

    private static void validateTestCase(ConfluenceConverterTestCase testCase) {
        assertTestCaseDataIsKnownType(testCase.getInput(), "input");
        assertTestCaseDataIsKnownType(testCase.getOutput(), "output");
    }

    private static void assertTestCaseDataIsKnownType(ConfluenceConverterTestCase.Data data, String field) {
        if (!(data instanceof ConfluenceConverterTestCase.Model || data instanceof ConfluenceConverterTestCase.Sdk)) {
            throw new IllegalStateException(String.format("%s was an unknown type: %s", field, data.getClass()));
        }
    }
}
