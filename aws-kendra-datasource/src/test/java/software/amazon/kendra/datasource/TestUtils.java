package software.amazon.kendra.datasource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

  public static final String STRING = "string";
  public static final List<String> STRING_LIST = Arrays.asList("string", "list");
  public static final Long LONG = (long) 12345;
  public static final String DATE_STRING = "2022-01-01T00:00:00Z";
  public static final Instant DATE_INSTANT = Instant.parse("2022-01-01T00:00:00Z");
  public static final String ATTRIBUTE_KEY = "key";
  public static final String CONDITION_OPERATOR = "Exists";
  public static final String LAMBDA_ARN = "aws::lambda:";
  public static final String S3_BUCKET = "bucket-name";
  public static final String ROLE_ARN = "aws::iam";

  public static DocumentAttributeValue provideModelDocumentAttributeValue() {
    return DocumentAttributeValue.builder().stringValue(STRING).build();
  }

  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeValue provideSdkDocumentAttributeValue() {
    return software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder().stringValue(STRING).build();
  }

  public static DocumentAttributeCondition provideModelCondition(){
    return DocumentAttributeCondition.builder()
        .conditionDocumentAttributeKey(ATTRIBUTE_KEY)
        .operator(CONDITION_OPERATOR)
        .build();
  }

  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition provideSdkCondition() {
    return software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition.builder()
        .conditionDocumentAttributeKey(ATTRIBUTE_KEY)
        .operator(CONDITION_OPERATOR)
        .build();
  }

  public static DocumentAttributeTarget provideModelDocumentAttributeTarget() {
    return DocumentAttributeTarget.builder()
        .targetDocumentAttributeKey(ATTRIBUTE_KEY)
        .targetDocumentAttributeValue(provideModelDocumentAttributeValue())
        .build();
  }

  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget provideSdkDocumentAttributeTarget() {
    return software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget.builder()
        .targetDocumentAttributeKey(ATTRIBUTE_KEY)
        .targetDocumentAttributeValue(provideSdkDocumentAttributeValue())
        .build();
  }

  public static InlineCustomDocumentEnrichmentConfiguration provideModelInlineCDE() {
    return InlineCustomDocumentEnrichmentConfiguration.builder()
        .target(provideModelDocumentAttributeTarget())
        .documentContentDeletion(true)
        .build();
  }

  public static software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration provideSdkInlineCDE() {
    return software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration.builder()
        .target(provideSdkDocumentAttributeTarget())
        .documentContentDeletion(true)
        .build();
  }

  public static HookConfiguration provideModelHookConfiguration(String lambdaArn) {
    return HookConfiguration.builder().lambdaArn(lambdaArn).s3Bucket(S3_BUCKET).build();
  }

  public static software.amazon.awssdk.services.kendra.model.HookConfiguration provideSdkHookConfiguration(String lambdaArn) {
    return software.amazon.awssdk.services.kendra.model.HookConfiguration.builder().lambdaArn(lambdaArn).s3Bucket(S3_BUCKET).build();
  }
}
