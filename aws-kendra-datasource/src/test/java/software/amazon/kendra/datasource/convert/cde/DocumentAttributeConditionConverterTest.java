package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.kendra.datasource.TestUtils.ATTRIBUTE_KEY;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.DocumentAttributeCondition;
import software.amazon.kendra.datasource.TestUtils;

public class DocumentAttributeConditionConverterTest {
  public static final List<String> OPERATORS = Arrays.asList(
      "GreaterThan",
      "GreaterThanOrEquals",
      "LessThan",
      "LessThanOrEquals",
      "Equals",
      "NotEquals",
      "Contains",
      "NotContains",
      "Exists",
      "NotExists",
      "BeginsWith");

  @Test
  void testToSdkAndToModelDocumentAttributeConditionNull() {
    assertThat(DocumentAttributeConditionConverter.toSdkDocumentAttributeCondition(null)).isNull();
    assertThat(DocumentAttributeConditionConverter.toModelDocumentAttributeCondition(null)).isNull();
  }

  @Test
  void testToSdkAndToModelDocumentAttributeCondition() {
    for(String operator : OPERATORS) {
      software.amazon.kendra.datasource.DocumentAttributeCondition modelDocumentAttributeTarget =
          DocumentAttributeCondition.builder()
              .conditionDocumentAttributeKey(ATTRIBUTE_KEY)
              .operator(operator)
              .conditionOnValue(TestUtils.provideModelDocumentAttributeValue())
              .build();
      software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition sdkDocumentAttributeTarget =
          software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition.builder()
              .conditionDocumentAttributeKey(ATTRIBUTE_KEY)
              .operator(operator)
              .conditionOnValue(TestUtils.provideSdkDocumentAttributeValue())
              .build();
      assertThat(DocumentAttributeConditionConverter.toSdkDocumentAttributeCondition(modelDocumentAttributeTarget))
          .isEqualTo(sdkDocumentAttributeTarget);
      assertThat(DocumentAttributeConditionConverter.toModelDocumentAttributeCondition(sdkDocumentAttributeTarget))
          .isEqualTo(modelDocumentAttributeTarget);
    }
  }

}
