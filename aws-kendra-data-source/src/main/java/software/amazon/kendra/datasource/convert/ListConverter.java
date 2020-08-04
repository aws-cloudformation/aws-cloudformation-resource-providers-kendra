package software.amazon.kendra.datasource.convert;

import java.util.List;
import java.util.stream.Collectors;

public class ListConverter {

    public static <T> List<T> toSdk(List<T> list) {
        if (list == null) {
            return null;
        }
        return list.stream().collect(Collectors.toList());
    }

    public static <T> List<T> toModel(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().collect(Collectors.toList());
    }
}
