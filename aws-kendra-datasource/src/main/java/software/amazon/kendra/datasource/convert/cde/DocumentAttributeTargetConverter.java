package software.amazon.kendra.datasource.convert.cde;

import software.amazon.kendra.datasource.DocumentAttributeTarget;

public class DocumentAttributeTargetConverter {

  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget toSdkDocumentAttributeTarget(
      final DocumentAttributeTarget documentAttributeTarget
  ) {
    if (documentAttributeTarget == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget.builder()
        .targetDocumentAttributeKey(documentAttributeTarget.getTargetDocumentAttributeKey())
        .targetDocumentAttributeValueDeletion(documentAttributeTarget.getTargetDocumentAttributeValueDeletion())
        .targetDocumentAttributeValue(DocumentAttributeValueConverter.toSdkDocumentAttributeValue(
            documentAttributeTarget.getTargetDocumentAttributeValue()))
        .build();
  }

  public static DocumentAttributeTarget toModelDocumentAttributeTarget(
      final software.amazon.awssdk.services.kendra.model.DocumentAttributeTarget documentAttributeTarget
  ) {
    if (documentAttributeTarget == null) {
      return null;
    }
    return DocumentAttributeTarget.builder()
        .targetDocumentAttributeKey(documentAttributeTarget.targetDocumentAttributeKey())
        .targetDocumentAttributeValueDeletion(documentAttributeTarget.targetDocumentAttributeValueDeletion())
        .targetDocumentAttributeValue(DocumentAttributeValueConverter.toModelDocumentAttributeValue(
            documentAttributeTarget.targetDocumentAttributeValue()))
        .build();
  }
}
