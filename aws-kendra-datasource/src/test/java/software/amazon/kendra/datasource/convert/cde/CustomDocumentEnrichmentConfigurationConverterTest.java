package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.kendra.datasource.TestUtils.ROLE_ARN;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.CustomDocumentEnrichmentConfiguration;
import software.amazon.kendra.datasource.TestUtils;

public class CustomDocumentEnrichmentConfigurationConverterTest {

  public static final String PRE_LAMBDA = "pre";
  public static final String POST_LAMBDA = "post";

  @Test
  void testToSdkAndToModelCustomDocumentEnrichmentConfigurationNull(){
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toSdkCustomDocumentEnrichmentConfiguration(null)).isNull();
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toModelCustomDocumentEnrichmentConfiguration(null)).isNull();
  }

  @Test
  void testToSdkAndToModelCustomDocumentEnrichmentConfigurationInlineOnly(){
    CustomDocumentEnrichmentConfiguration modelCDEConfiguration =
        CustomDocumentEnrichmentConfiguration.builder()
            .inlineConfigurations(Collections.singletonList(TestUtils.provideModelInlineCDE()))
            .build();
    software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration sdkCDEConfiguration =
        software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration.builder()
            .inlineConfigurations(Collections.singletonList(TestUtils.provideSdkInlineCDE()))
            .build();
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toSdkCustomDocumentEnrichmentConfiguration(modelCDEConfiguration)).isEqualTo(sdkCDEConfiguration);
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toModelCustomDocumentEnrichmentConfiguration(sdkCDEConfiguration)).isEqualTo(modelCDEConfiguration);
  }

  @Test
  void testToSdkAndToModelCustomDocumentEnrichmentConfigurationPreHookOnly(){
    CustomDocumentEnrichmentConfiguration modelCDEConfiguration =
        CustomDocumentEnrichmentConfiguration.builder()
            .preExtractionHookConfiguration(TestUtils.provideModelHookConfiguration(PRE_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration sdkCDEConfiguration =
        software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration.builder()
            .preExtractionHookConfiguration(TestUtils.provideSdkHookConfiguration(PRE_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toSdkCustomDocumentEnrichmentConfiguration(modelCDEConfiguration)).isEqualTo(sdkCDEConfiguration);
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toModelCustomDocumentEnrichmentConfiguration(sdkCDEConfiguration)).isEqualTo(modelCDEConfiguration);
  }

  @Test
  void testToSdkAndToModelCustomDocumentEnrichmentConfigurationPostHookOnly(){
    CustomDocumentEnrichmentConfiguration modelCDEConfiguration =
        CustomDocumentEnrichmentConfiguration.builder()
            .postExtractionHookConfiguration(TestUtils.provideModelHookConfiguration(POST_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration sdkCDEConfiguration =
        software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration.builder()
            .postExtractionHookConfiguration(TestUtils.provideSdkHookConfiguration(POST_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toSdkCustomDocumentEnrichmentConfiguration(modelCDEConfiguration)).isEqualTo(sdkCDEConfiguration);
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toModelCustomDocumentEnrichmentConfiguration(sdkCDEConfiguration)).isEqualTo(modelCDEConfiguration);
  }

  @Test
  void testToSdkAndToModelCustomDocumentEnrichmentConfigurationAll(){
    CustomDocumentEnrichmentConfiguration modelCDEConfiguration =
        CustomDocumentEnrichmentConfiguration.builder()
            .inlineConfigurations(Collections.singletonList(TestUtils.provideModelInlineCDE()))
            .preExtractionHookConfiguration(TestUtils.provideModelHookConfiguration(PRE_LAMBDA))
            .postExtractionHookConfiguration(TestUtils.provideModelHookConfiguration(POST_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration sdkCDEConfiguration =
        software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration.builder()
            .inlineConfigurations(Collections.singletonList(TestUtils.provideSdkInlineCDE()))
            .preExtractionHookConfiguration(TestUtils.provideSdkHookConfiguration(PRE_LAMBDA))
            .postExtractionHookConfiguration(TestUtils.provideSdkHookConfiguration(POST_LAMBDA))
            .roleArn(ROLE_ARN)
            .build();
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toSdkCustomDocumentEnrichmentConfiguration(modelCDEConfiguration)).isEqualTo(sdkCDEConfiguration);
    assertThat(CustomDocumentEnrichmentConfigurationConverter.toModelCustomDocumentEnrichmentConfiguration(sdkCDEConfiguration)).isEqualTo(modelCDEConfiguration);
  }
}
