package software.amazon.kendra.datasource.convert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListConverter {

   public static <I, O> List<O> toSdk(List<I> list, Function<I, O> func) {
      if (list == null) {
         return null;
      }
      return list.stream().map(x -> func.apply(x)).collect(Collectors.toList());
   }

   public static <I, O> List<O> toModel(List<I> list, Function<I, O> func) {
      if (list == null || list.isEmpty()) {
         return null;
      }
      return list.stream().map(x -> func.apply(x)).collect(Collectors.toList());
   }
}
