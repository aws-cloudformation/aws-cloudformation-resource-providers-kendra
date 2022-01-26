package software.amazon.kendra.datasource.convert.cde;

import software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition;

public class DocumentAttributeConditionConverter {

  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition toSdkDocumentAttributeCondition(
      final software.amazon.kendra.datasource.DocumentAttributeCondition documentAttributeCondition) {
    if (documentAttributeCondition == null) {
      return null;
    }
    return DocumentAttributeCondition.builder()
        .conditionDocumentAttributeKey(documentAttributeCondition.getConditionDocumentAttributeKey())
        .operator(software.amazon.awssdk.services.kendra.model.ConditionOperator.fromValue(documentAttributeCondition.getOperator()))
        .conditionOnValue(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(documentAttributeCondition.getConditionOnValue()))
        .build();
  }

  public static software.amazon.kendra.datasource.DocumentAttributeCondition toModelDocumentAttributeCondition(
      final software.amazon.awssdk.services.kendra.model.DocumentAttributeCondition documentAttributeCondition) {
    if (documentAttributeCondition == null) {
      return null;
    }
    return software.amazon.kendra.datasource.DocumentAttributeCondition.builder()
        .conditionDocumentAttributeKey(documentAttributeCondition.conditionDocumentAttributeKey())
        .operator(documentAttributeCondition.operator().toString())
        .conditionOnValue(DocumentAttributeValueConverter.toModelDocumentAttributeValue(documentAttributeCondition.conditionOnValue()))
        .build();
  }
}
