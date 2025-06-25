package software.amazon.kendra.datasource.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.core.document.Document;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;

class TemplateConverterTest {

  @Test
  public void testMapToDocumentConversion() {
    Map<String, Object> inputMap = ImmutableMap.<String, Object>builder()
        .put("stringKey", "hello")
        .put("numberKey", 42)
        .put("booleanStringKey", "true")
        .put("booleanStringKeyFalse", "false")
        .put("nestedObject", ImmutableMap.of(
            "nestedString", "nested value",
            "nestedNumber", 123,
            "nestedBoolean", "true"
        ))
        .put("stringList", ImmutableList.of("one", "two", "three"))
        .put("numberList", ImmutableList.of(1, 2, 3))
        .put("booleanList", ImmutableList.of("true", "false", "true"))
        .put("objectList", ImmutableList.of(
            ImmutableMap.of("id", 1, "name", "first"),
            ImmutableMap.of("id", 2, "name", "second")
        ))
        .build();

    TemplateConfiguration modelTempl = TemplateConfiguration.builder()
        .template(inputMap)
        .build();

    software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdkTemplate
        = TemplateConverter.toSdkTemplateConfiguration(modelTempl);

    Document result = sdkTemplate.template();

    // verify
    assertThat(result).isNotNull();
    assertThat(result.isMap()).isTrue();
    Map<String, Document> resultMap = result.asMap();

    // Original assertions
    assertTrue(resultMap.get("stringKey").isString());
    assertThat(resultMap.get("stringKey").asString()).isEqualTo("hello");

    assertTrue(resultMap.get("numberKey").isNumber());
    assertThat(resultMap.get("numberKey").asNumber().intValue()).isEqualTo(42);

    assertTrue(resultMap.get("booleanStringKey").isBoolean());
    assertTrue(resultMap.get("booleanStringKey").asBoolean());

    assertTrue(resultMap.get("booleanStringKeyFalse").isBoolean());
    assertFalse(resultMap.get("booleanStringKeyFalse").asBoolean());

    // Nested object assertions
    Document nestedDoc = resultMap.get("nestedObject");
    assertTrue(nestedDoc.isMap());
    Map<String, Document> nestedResultMap = nestedDoc.asMap();
    assertThat(nestedResultMap.get("nestedString").asString()).isEqualTo("nested value");
    assertThat(nestedResultMap.get("nestedNumber").asNumber().intValue()).isEqualTo(123);
    assertTrue(nestedResultMap.get("nestedBoolean").asBoolean());

    // List assertions
    Document stringListDoc = resultMap.get("stringList");
    assertTrue(stringListDoc.isList());
    List<Document> stringList = stringListDoc.asList();
    assertThat(stringList).hasSize(3);
    assertThat(stringList.get(0).asString()).isEqualTo("one");
    assertThat(stringList.get(1).asString()).isEqualTo("two");
    assertThat(stringList.get(2).asString()).isEqualTo("three");

    Document numberListDoc = resultMap.get("numberList");
    assertTrue(numberListDoc.isList());
    List<Document> numberList = numberListDoc.asList();
    assertThat(numberList).hasSize(3);
    assertThat(numberList.get(0).asNumber().intValue()).isEqualTo(1);
    assertThat(numberList.get(1).asNumber().intValue()).isEqualTo(2);
    assertThat(numberList.get(2).asNumber().intValue()).isEqualTo(3);

    Document booleanListDoc = resultMap.get("booleanList");
    assertTrue(booleanListDoc.isList());
    List<Document> booleanList = booleanListDoc.asList();
    assertThat(booleanList).hasSize(3);
    assertTrue(booleanList.get(0).asBoolean());
    assertFalse(booleanList.get(1).asBoolean());
    assertTrue(booleanList.get(2).asBoolean());

    // Object list assertions
    Document objectListDoc = resultMap.get("objectList");
    assertTrue(objectListDoc.isList());
    List<Document> objectListResult = objectListDoc.asList();
    assertThat(objectListResult).hasSize(2);

    Map<String, Document> firstObject = objectListResult.get(0).asMap();
    assertThat(firstObject.get("id").asNumber().intValue()).isEqualTo(1);
    assertThat(firstObject.get("name").asString()).isEqualTo("first");

    Map<String, Document> secondObject = objectListResult.get(1).asMap();
    assertThat(secondObject.get("id").asNumber().intValue()).isEqualTo(2);
    assertThat(secondObject.get("name").asString()).isEqualTo("second");
  }


  @Test
  public void testDocumentToMapConversion() {
    // Create a Document structure without number documents
    Document doc = Document.fromMap(ImmutableMap.<String, Document>builder()
        .put("boolKey", Document.fromBoolean(true))
        .put("stringKey", Document.fromString("hello"))
        .put("stringNumber", Document.fromString("42"))
        .put("nestedObject", Document.fromMap(ImmutableMap.of(
            "nestedString", Document.fromString("nested value"),
            "nestedStringNumber", Document.fromString("123"),
            "nestedBoolean", Document.fromBoolean(true)
        )))
        .put("stringList", Document.fromList(ImmutableList.of(
            Document.fromString("one"),
            Document.fromString("two"),
            Document.fromString("three")
        )))
        .put("booleanList", Document.fromList(ImmutableList.of(
            Document.fromBoolean(true),
            Document.fromBoolean(false),
            Document.fromBoolean(true)
        )))
        .put("objectList", Document.fromList(ImmutableList.of(
            Document.fromMap(ImmutableMap.of(
                "id", Document.fromString("1"),
                "name", Document.fromString("first")
            )),
            Document.fromMap(ImmutableMap.of(
                "id", Document.fromString("2"),
                "name", Document.fromString("second")
            ))
        )))
        .build());

    software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdkTemplate =
        software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
            .template(doc)
            .build();
    DataSourceConfiguration modelConf = TemplateConverter.toModelDataSourceConfiguration(sdkTemplate);
    Map<String, Object> resultMap = modelConf.getTemplateConfiguration().getTemplate();

    // Verify
    assertThat(resultMap.get("boolKey")).isEqualTo(true);
    assertThat(resultMap.get("stringKey")).isEqualTo("hello");
    assertThat(resultMap.get("stringNumber")).isEqualTo("42");

    // Verify nested object
    @SuppressWarnings("unchecked")
    Map<String, Object> nestedMap = (Map<String, Object>) resultMap.get("nestedObject");
    assertThat(nestedMap.get("nestedString")).isEqualTo("nested value");
    assertThat(nestedMap.get("nestedStringNumber")).isEqualTo("123");
    assertThat(nestedMap.get("nestedBoolean")).isEqualTo(true);

    // Verify lists
    @SuppressWarnings("unchecked")
    List<String> stringList = (List<String>) resultMap.get("stringList");
    assertThat(stringList).containsExactly("one", "two", "three");

    @SuppressWarnings("unchecked")
    List<Boolean> booleanList = (List<Boolean>) resultMap.get("booleanList");
    assertThat(booleanList).containsExactly(true, false, true);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> objectList = (List<Map<String, Object>>) resultMap.get("objectList");
    assertThat(objectList).hasSize(2);
    assertThat(objectList.get(0)).containsEntry("id", "1").containsEntry("name", "first");
    assertThat(objectList.get(1)).containsEntry("id", "2").containsEntry("name", "second");
  }
}
