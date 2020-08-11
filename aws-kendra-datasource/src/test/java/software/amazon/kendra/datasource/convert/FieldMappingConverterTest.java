package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldMappingConverterTest {

    @Test
    void testToModel() {
        assertThat(FieldMappingConverter.toModel(null)).isNull();
    }

    @Test
    void testToSdk() {
        assertThat(FieldMappingConverter.toSdk(null)).isNull();
    }

}
