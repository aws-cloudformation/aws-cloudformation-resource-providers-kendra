package software.amazon.kendra.datasource.convert.webcrawler;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class WebCrawlerConverterTestCase {

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
    public WebCrawlerConverterTestCase symmetricalTestCase() {
        return WebCrawlerConverterTestCase.builder()
                .input(expectedOutput)
                .expectedOutput(input)
                .isSymmetrical(isSymmetrical)
                .description(String.format("Symmetrical(%s)", description))
                .build();
    }

    public interface Data {
        /**
         * True iff this is a SdkData class.
         */
        default boolean isSdk() {
            return this instanceof WebCrawlerConverterTestCase.SdkData;
        }

        /**
         * Returns this class as an SDK WebCrawlerConfiguration iff it is an instanceof SdkData.
         */
        default software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration asSdk() {
            throw new IllegalStateException("Not an SdkData");
        }

        /**
         * Returns this class as an RPK WebCrawlerConfiguration iff it is an instanceof ModelData.
         */
        default software.amazon.kendra.datasource.WebCrawlerConfiguration asModel() {
            throw new IllegalStateException("Not a ModelData");
        }
    }

    /**
     * An Sdk representation of a WebCrawlerConfiguration.
     */
    @Value
    public static final class SdkData implements WebCrawlerConverterTestCase.Data {

        private final software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration data;

        @Override
        public software.amazon.awssdk.services.kendra.model.WebCrawlerConfiguration asSdk() {
            return this.data;
        }
    }

    /**
     * An RPK representation of a WebCrawlerConfiguration.
     */
    @Value
    public static final class ModelData implements WebCrawlerConverterTestCase.Data {

        private final software.amazon.kendra.datasource.WebCrawlerConfiguration data;

        @Override
        public software.amazon.kendra.datasource.WebCrawlerConfiguration asModel() {
            return this.data;
        }
    }
}
