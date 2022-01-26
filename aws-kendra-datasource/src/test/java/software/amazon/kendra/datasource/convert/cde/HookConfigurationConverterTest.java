package software.amazon.kendra.datasource.convert.cde;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.kendra.datasource.TestUtils.LAMBDA_ARN;
import static software.amazon.kendra.datasource.TestUtils.S3_BUCKET;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.HookConfiguration;
import software.amazon.kendra.datasource.TestUtils;

public class HookConfigurationConverterTest {



  @Test
  void testToSdkAndToModelHookConfigurationNull() {
    assertThat(HookConfigurationConverter.toSdkHookConfiguration(null)).isNull();
    assertThat(HookConfigurationConverter.toModelHookConfiguration(null)).isNull();
  }

  @Test
  void testToSdkAndToModelHookConfigurationNoCondition() {
    software.amazon.kendra.datasource.HookConfiguration modelDocumentAttributeValue =
        HookConfiguration.builder()
            .lambdaArn(LAMBDA_ARN)
            .s3Bucket(S3_BUCKET)
            .build();
    software.amazon.awssdk.services.kendra.model.HookConfiguration sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.HookConfiguration.builder()
            .s3Bucket(S3_BUCKET)
            .lambdaArn(LAMBDA_ARN)
            .build();
    assertThat(HookConfigurationConverter.toSdkHookConfiguration(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(HookConfigurationConverter.toModelHookConfiguration(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }

  @Test
  void testToSdkAndToModelHookConfigurationWithCondition() {
    software.amazon.kendra.datasource.HookConfiguration modelDocumentAttributeValue =
        HookConfiguration.builder()
            .lambdaArn(LAMBDA_ARN)
            .s3Bucket(S3_BUCKET)
            .invocationCondition(TestUtils.provideModelCondition())
            .build();
    software.amazon.awssdk.services.kendra.model.HookConfiguration sdkDocumentAttributeValue =
        software.amazon.awssdk.services.kendra.model.HookConfiguration.builder()
            .s3Bucket(S3_BUCKET)
            .lambdaArn(LAMBDA_ARN)
            .invocationCondition(TestUtils.provideSdkCondition())
            .build();
    assertThat(HookConfigurationConverter.toSdkHookConfiguration(modelDocumentAttributeValue))
        .isEqualTo(sdkDocumentAttributeValue);
    assertThat(HookConfigurationConverter.toModelHookConfiguration(sdkDocumentAttributeValue))
        .isEqualTo(modelDocumentAttributeValue);
  }
}
