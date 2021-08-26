package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberConverterTest {

    @Test
    void testDoubleToFloat() {
        Double valSmall = 0.000001;
        Float expectedValSmall = 0.000001f;

        Double valLarge = 50.0;
        Float expectedValLarge = 50.0f;

        assertThat(NumberConverter.doubleToFloat(valSmall)).isEqualTo(expectedValSmall);
        assertThat(NumberConverter.doubleToFloat(valLarge)).isEqualTo(expectedValLarge);
    }

    @Test
    void testFloatToDouble() {
        Float valSmall = 0.000001f;
        Double expectedValSmall = 0.000001;

        Float valLarge = 50.0f;
        Double expectedValLarge = 50.0;

        assertThat(NumberConverter.floatToDouble(valSmall)).isEqualTo(expectedValSmall);
        assertThat(NumberConverter.floatToDouble(valLarge)).isEqualTo(expectedValLarge);
    }
}
