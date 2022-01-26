package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.kendra.datasource.TestUtils.DATE_INSTANT;
import static software.amazon.kendra.datasource.TestUtils.DATE_STRING;
import static software.amazon.kendra.datasource.TestUtils.LONG;
import static software.amazon.kendra.datasource.TestUtils.STRING;
import static software.amazon.kendra.datasource.TestUtils.STRING_LIST;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.DocumentAttributeValue;

public class DocumentAttributeValueConverterTest {



  @Test
  void testToSdkAndToModelDocumentAttributeNull() {
    assertThat(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(null)).isNull();
    assertThat(DocumentAttributeValueConverter.toModelDocumentAttributeValue(null)).isNull();
  }

  @Test
  void testToSdkAndToModelDocumentAttributeValueStringValue() {
    DocumentAttributeValue modelDocumentAttributeValue =
        DocumentAttributeValue.builder()
            .stringValue(STRING)
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeValue sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder()
            .stringValue(STRING)
            .build();
    assertThat(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(DocumentAttributeValueConverter.toModelDocumentAttributeValue(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelDocumentAttributeValueStringListValue() {
    software.amazon.kendra.datasource.DocumentAttributeValue modelDocumentAttributeValue =
        DocumentAttributeValue.builder()
            .stringListValue(STRING_LIST)
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeValue sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder()
            .stringListValue(STRING_LIST)
            .build();
    assertThat(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(DocumentAttributeValueConverter.toModelDocumentAttributeValue(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelDocumentAttributeValueLongValue() {
    software.amazon.kendra.datasource.DocumentAttributeValue modelDocumentAttributeValue =
        DocumentAttributeValue.builder()
            .longValue(LONG)
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeValue sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder()
            .longValue(LONG)
            .build();
    assertThat(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(DocumentAttributeValueConverter.toModelDocumentAttributeValue(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelDocumentAttributeValueDateValue() {
    software.amazon.kendra.datasource.DocumentAttributeValue modelDocumentAttributeValue =
        DocumentAttributeValue.builder()
            .dateValue(DATE_STRING)
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeValue sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder()
            .dateValue(DATE_INSTANT)
            .build();

    assertThat(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(DocumentAttributeValueConverter.toModelDocumentAttributeValue(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }
}
