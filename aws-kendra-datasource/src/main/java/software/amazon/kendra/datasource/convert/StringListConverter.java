package software.amazon.kendra.datasource.convert;

import java.util.List;
import java.util.function.Function;

// Helper class for string lists that need to be converted.
public class StringListConverter {

    public static List<String> toSdk(List<String> list) {
        return ListConverter.toSdk(list, Function.identity());
    }

    public static List<String> toModel(List<String> list) {
        return ListConverter.toModel(list, Function.identity());
    }
}
