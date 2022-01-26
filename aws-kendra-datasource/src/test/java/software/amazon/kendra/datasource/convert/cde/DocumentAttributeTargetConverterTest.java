package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.kendra.datasource.TestUtils.ATTRIBUTE_KEY;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.DocumentAttributeTarget;
import software.amazon.kendra.datasource.TestUtils;

public class DocumentAttributeTargetConverterTest {

  @Test
  void testToSdkAndToModelDocumentAttributeTargetNull() {
    assertThat(DocumentAttributeTargetConverter.toSdkDocumentAttributeTarget(null)).isNull();
    assertThat(DocumentAttributeTargetConverter.toModelDocumentAttributeTarget(null)).isNull();
  }

  @Test
  void testToSdkAndToModelDocumentAttributeTargetValueDeletion() {
    software.amazon.kendra.datasource.DocumentAttributeTarget modelDocumentAttributeTarget =
        DocumentAttributeTarget.builder()
            .targetDocumentAttributeKey(ATTRIBUTE_KEY)
            .targetDocumentAttributeValueDeletion(true)
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget sdkDocumentAttributeTarget =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget.builder()
            .targetDocumentAttributeKey(ATTRIBUTE_KEY)
            .targetDocumentAttributeValueDeletion(true)
            .build();
    assertThat(DocumentAttributeTargetConverter.toSdkDocumentAttributeTarget(modelDocumentAttributeTarget))
        .isEqualTo(sdkDocumentAttributeTarget);
    assertThat(DocumentAttributeTargetConverter.toModelDocumentAttributeTarget(sdkDocumentAttributeTarget))
        .isEqualTo(modelDocumentAttributeTarget);
  }

  @Test
  void testToSdkAndToModelDocumentAttributeTargetNewValue() {
    software.amazon.kendra.datasource.DocumentAttributeTarget modelDocumentAttributeTarget =
        DocumentAttributeTarget.builder()
            .targetDocumentAttributeKey(ATTRIBUTE_KEY)
            .targetDocumentAttributeValue(TestUtils.provideModelDocumentAttributeValue())
            .build();
    software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget sdkDocumentAttributeTarget =
        software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget.builder()
            .targetDocumentAttributeKey(ATTRIBUTE_KEY)
            .targetDocumentAttributeValue(TestUtils.provideSdkDocumentAttributeValue())
            .build();
    assertThat(DocumentAttributeTargetConverter.toSdkDocumentAttributeTarget(modelDocumentAttributeTarget))
        .isEqualTo(sdkDocumentAttributeTarget);
    assertThat(DocumentAttributeTargetConverter.toModelDocumentAttributeTarget(sdkDocumentAttributeTarget))
        .isEqualTo(modelDocumentAttributeTarget);
  }
}
