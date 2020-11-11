package software.amazon.kendra.datasource.convert;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import software.amazon.kendra.datasource.ConfluenceConfiguration;

@Value
@Builder
public class ConfluenceConverterTestCase {

    @NonNull private String description;
    // nullable boolean ensures some value is always set
    @NonNull private Boolean reflexive;
    @NonNull private Data input;
    @NonNull private Data output;

    public static abstract class Data {

        public ConfluenceConfiguration asModel() {
            throw new UnsupportedOperationException();
        }

        public software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration asSdk() {
            throw new UnsupportedOperationException();
        }
    }

    @Value
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static final class Model extends Data {

        private ConfluenceConfiguration model;

        @Override
        public ConfluenceConfiguration asModel() {
            return model;
        }
    }

    @Value
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static final class Sdk extends Data {

        private software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration sdk;

        @Override
        public software.amazon.awssdk.services.kendra.model.ConfluenceConfiguration asSdk() {
            return sdk;
        }
    }
}
