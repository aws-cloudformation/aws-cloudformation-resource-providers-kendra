package software.amazon.kendra.datasource.convert.cde;

import software.amazon.kendra.datasource.HookConfiguration;

public class HookConfigurationConverter {
  public static software.amazon.awssdk.services.kendra.model.HookConfiguration toSdkHookConfiguration(
      final HookConfiguration hookConfiguration) {
    if (hookConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.HookConfiguration.builder()
        .invocationCondition(DocumentAttributeConditionConverter.toSdkDocumentAttributeCondition(hookConfiguration.getInvocationCondition()))
        .lambdaArn(hookConfiguration.getLambdaArn())
        .s3Bucket(hookConfiguration.getS3Bucket())
        .build();
  }

  public static HookConfiguration toModelHookConfiguration(
      final software.amazon.awssdk.services.kendra.model.HookConfiguration hookConfiguration) {
    if (hookConfiguration == null) {
      return null;
    }
    return HookConfiguration.builder()
        .invocationCondition(DocumentAttributeConditionConverter.toModelDocumentAttributeCondition(hookConfiguration.invocationCondition()))
        .lambdaArn(hookConfiguration.lambdaArn())
        .s3Bucket(hookConfiguration.s3Bucket())
        .build();
  }
}
