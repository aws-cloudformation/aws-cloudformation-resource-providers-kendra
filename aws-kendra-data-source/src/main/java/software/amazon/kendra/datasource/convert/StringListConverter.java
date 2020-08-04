package software.amazon.kendra.datasource.convert;

import java.util.List;
import java.util.stream.Collectors;

public class StringListConverter {

    public static List<String> toSdk(List<String> list) {
        if (list == null) {
            return null;
        }
        return list.stream().collect(Collectors.toList());
    }

    public static List<String> toModel(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().collect(Collectors.toList());
    }
}
