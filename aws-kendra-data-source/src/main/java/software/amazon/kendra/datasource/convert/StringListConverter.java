package software.amazon.kendra.datasource.convert;

import java.util.List;
import java.util.stream.Collectors;

public class StringListConverter {

    public static List<String> toSdkStringList(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return stringList.stream().collect(Collectors.toList());
    }

    public static List<String> toModelStringList(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        return stringList.stream().collect(Collectors.toList());
    }
}
