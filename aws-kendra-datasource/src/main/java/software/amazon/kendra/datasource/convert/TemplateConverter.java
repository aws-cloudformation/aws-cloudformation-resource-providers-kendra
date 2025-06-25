package software.amazon.kendra.datasource.convert;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import software.amazon.awssdk.core.document.Document;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;

public class TemplateConverter {

  public static software.amazon.awssdk.services.kendra.model.TemplateConfiguration toSdkTemplateConfiguration(
      TemplateConfiguration model
  ) {
    if (model == null || model.getTemplate() == null) {
      return null;
    }

    return software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
        .template(templateMapToDocument(model.getTemplate()))
        .build();
  }

  public static DataSourceConfiguration toModelDataSourceConfiguration(
      software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdkTemplateConfiguration
  ) {
    if (sdkTemplateConfiguration == null || sdkTemplateConfiguration.template() == null) {
      return null;
    }

    return DataSourceConfiguration.builder()
        .templateConfiguration(TemplateConfiguration.builder()
            .template(documentToTemplateMap(sdkTemplateConfiguration.template()))
            .build()
        )
        .build();
  }

  private static Document templateMapToDocument(
      Map<String, Object> templateMap
  ) {
    if (templateMap == null) {
      return null;
    }

    ImmutableMap.Builder<String, Document> mapBuilder = ImmutableMap.builder();

    for (Map.Entry<String, Object> mapEntry : templateMap.entrySet()) {
      String key = mapEntry.getKey();
      Object value = mapEntry.getValue();

      mapBuilder.put(key, objectToDocument(value));
    }

    return Document.fromMap(mapBuilder.build());
  }

  private static Map<String, Object> documentToTemplateMap(
      Document document
  ) {
    if (Objects.isNull(document)) {
      return null;
    }
    if (!document.isMap()) {
      throw new CfnGeneralServiceException("Upstream service returned an unexpected template document.");
    }

    ImmutableMap.Builder<String, Object> outputMapBuilder = ImmutableMap.builder();
    for (Map.Entry<String, Document> documentEntry : document.asMap().entrySet()) {
      String key = documentEntry.getKey();
      // Note: This call to unwrap will give us a String for NumberDocument (Document.fromNumber(42).unwrap() -> "42").
      // At the moment, all the datasource schemas specify their numbers as strings anyway. So this
      // makes no difference. But if that changes in the future, then this method will need to switch on the document type for precision
      Object value = documentEntry.getValue().unwrap();
      outputMapBuilder.put(key, value);
    }

    return outputMapBuilder.build();
  }

  private static Document objectToDocument(Object value) {
    if (value instanceof Boolean) {
      return Document.fromBoolean((Boolean) value);
    } else if (value instanceof String) {
      String string = (String) value;
      // Due to how yaml handles values, we'll receive boolean values as strings.
      if ("true".equals(string) || "false".equals(string)) {
        boolean boolValue = Boolean.parseBoolean(string);
        return Document.fromBoolean(boolValue);
      }
      return Document.fromString(string);
    } else if (value instanceof Number) {
      if (value instanceof Integer) {
        return Document.fromNumber((Integer) value);
      } else if (value instanceof Long) {
        return Document.fromNumber((Long) value);
      } else if (value instanceof Double) {
        Double doubleVal = (Double) value;
        return Document.fromNumber(doubleVal);
      } else {
        String error = String.format(
            "Unexpected number type found: %s. Expecting Integer, Long, or Double values only.",
            value
        );
        throw new CfnInvalidRequestException(error);
      }
    } else if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) value;

      ImmutableList.Builder<Document> converted = ImmutableList.builder();
      for (Object item : list) {
        converted.add(objectToDocument(item));
      }
      return Document.fromList(converted.build());
    } else if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> rawMap = (Map<String, Object>) value;
      return templateMapToDocument(rawMap);
    } else {
      throw new CfnInvalidRequestException(String.format("Unexpected document value found: %s", value));
    }
  }

}
