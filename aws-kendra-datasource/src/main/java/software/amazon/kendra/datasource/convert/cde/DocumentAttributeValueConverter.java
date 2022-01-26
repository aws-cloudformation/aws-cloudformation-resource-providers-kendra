package software.amazon.kendra.datasource.convert.cde;

import java.time.Instant;

import software.amazon.kendra.datasource.DocumentAttributeValue;
import software.amazon.kendra.datasource.convert.StringListConverter;

public class DocumentAttributeValueConverter {
  public static software.amazon.awssdk.services.kendra.model.DocumentAttributeValue toSdkDocumentAttributeValue(
      final DocumentAttributeValue documentAttributeValue
  ) {
    if (documentAttributeValue == null){
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.DocumentAttributeValue.builder()
        .stringValue(documentAttributeValue.getStringValue())
        .stringListValue(StringListConverter.toSdk(documentAttributeValue.getStringListValue()))
        .longValue(documentAttributeValue.getLongValue())
        .dateValue(documentAttributeValue.getDateValue() == null? null : Instant.parse(documentAttributeValue.getDateValue()))
        .build();
  }

  public static DocumentAttributeValue toModelDocumentAttributeValue(
      software.amazon.awssdk.services.kendra.model.DocumentAttributeValue documentAttributeValue) {
    if (documentAttributeValue == null){
      return null;
    }
    return DocumentAttributeValue.builder()
        .stringValue(documentAttributeValue.stringValue())
        .stringListValue(StringListConverter.toModel(documentAttributeValue.stringListValue()))
        .longValue(documentAttributeValue.longValue())
        .dateValue(documentAttributeValue.dateValue() == null? null : documentAttributeValue.dateValue().toString())
        .build();
  }
}
