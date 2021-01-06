package software.amazon.kendra.datasource.convert.gdrive;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * GoogleDriveConverterTestCase represents a test case to be used in GoogleDriveConverterTest.
 *
 * Each instance of this class represents a conversion between
 * sdk -> model or model -> sdk depending on the types of input and expectedOutput.
 *
 * If an instance of this class is defined to be symmetrical (which is true by default), then it states that
 * conversions in the opposite direction (eg output -> input) is a valid test case.
 *
 * @see GoogleDriveConverterTest - how this class is used
 * @see GoogleDriveConverterTestCases - collection of test cases
 */
@Value
@Builder
public class GoogleDriveConverterTestCase {

    // human readable description of this test case
    @NonNull private final String description;

    // given this data
    @NonNull private final Data input;

    // the converter should return this data
    @NonNull private final Data expectedOutput;

    // if this is true, the test case can also be done in reverse
    @Builder.Default
    private final boolean isSymmetrical = true;

    /**
     * Produces a test case that is identical to this test case, but in the opposite direction.
     */
    public GoogleDriveConverterTestCase symmetricalTestCase() {
        return GoogleDriveConverterTestCase.builder()
            .input(expectedOutput)
            .expectedOutput(input)
            .isSymmetrical(isSymmetrical)
            .description(String.format("Symmetrical(%s)", description))
            .build();
    }

    /**
     * Data represents a either a SDK GoogleDriveConfiguration or RPK GoogleDriveConfiguration.
     * @see SdkData - sdk configuration
     * @see ModelData - rpk configuration
     */
    public interface Data {

        /**
         * True iff this is a SdkData class.
         */
        default boolean isSdk() {
            return this instanceof SdkData;
        }

        /**
         * Returns this class as an SDK GoogleDriveConfiguration iff it is an instanceof SdkData.
         */
        default software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration asSdk() {
            throw new IllegalStateException("Not an SdkData");
        }

        /**
         * Returns this class as an RPK GoogleDriveConfiguration iff it is an instanceof ModelData.
         */
        default software.amazon.kendra.datasource.GoogleDriveConfiguration asModel() {
            throw new IllegalStateException("Not a ModelData");
        }
    }

    /**
     * An Sdk representation of a GoogleDriveConfiguration.
     */
    @Value
    public static final class SdkData implements Data {

        private final software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration data;

        @Override
        public software.amazon.awssdk.services.kendra.model.GoogleDriveConfiguration asSdk() {
            return this.data;
        }
    }

    /**
     * An RPK representation of a GoogleDriveConfiguration.
     */
    @Value
    public static final class ModelData implements Data {

        private final software.amazon.kendra.datasource.GoogleDriveConfiguration data;

        @Override
        public software.amazon.kendra.datasource.GoogleDriveConfiguration asModel() {
            return this.data;
        }
    }
}
