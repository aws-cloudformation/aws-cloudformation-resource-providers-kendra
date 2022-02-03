package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.InlineCustomDocumentEnrichmentConfiguration;
import software.amazon.kendra.datasource.TestUtils;

public class InlineCustomDocumentEnrichmentConfigurationConverterTest {

  @Test
  void testToSdkAndToModelInclineCDEConfigurationNull() {
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toSdkInlineCDEConfiguration(null)).isNull();
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toModelInlineCDEConfiguration(null)).isNull();
  }

  @Test
  void testToSdkAndToModelInclineCDEConfigurationContentDeletion() {
    software.amazon.kendra.datasource.InlineCustomDocumentEnrichmentConfiguration modelDocumentAttributeValue =
        InlineCustomDocumentEnrichmentConfiguration.builder()
            .documentContentDeletion(true)
            .build();
    software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration.builder()
            .documentContentDeletion(true)
            .build();
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toSdkInlineCDEConfiguration(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toModelInlineCDEConfiguration(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelInclineCDEConfigurationNoCondition() {
    software.amazon.kendra.datasource.InlineCustomDocumentEnrichmentConfiguration modelDocumentAttributeValue =
        InlineCustomDocumentEnrichmentConfiguration.builder()
            .target(TestUtils.provideModelDocumentAttributeTarget())
            .build();
    software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration.builder()
            .target(TestUtils.provideSdkDocumentAttributeTarget())
            .build();
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toSdkInlineCDEConfiguration(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toModelInlineCDEConfiguration(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelInclineCDEConfigurationWithCondition() {
    software.amazon.kendra.datasource.InlineCustomDocumentEnrichmentConfiguration modelDocumentAttributeValue =
        InlineCustomDocumentEnrichmentConfiguration.builder()
            .target(TestUtils.provideModelDocumentAttributeTarget())
            .condition(TestUtils.provideModelCondition())
            .build();
    software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration.builder()
            .target(TestUtils.provideSdkDocumentAttributeTarget())
            .condition(TestUtils.provideSdkCondition())
            .build();
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toSdkInlineCDEConfiguration(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(InlineCustomDocumentEnrichmentConfigurationConverter.toModelInlineCDEConfiguration(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }
}
