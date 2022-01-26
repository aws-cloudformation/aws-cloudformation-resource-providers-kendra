package software.amazon.kendra.datasource.convert.cde;

import software.amazon.kendra.datasource.InlineCustomDocumentEnrichmentConfiguration;

public class InlineCustomDocumentEnrichmentConfigurationConverter {
  public static software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration toSdkInlineCDEConfiguration(
      final InlineCustomDocumentEnrichmentConfiguration inlineCDEConfiguration
  ) {
    if (inlineCDEConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration.builder()
        .condition(DocumentAttributeConditionConverter.toSdkDocumentAttributeCondition(inlineCDEConfiguration.getCondition()))
        .documentContentDeletion(inlineCDEConfiguration.getDocumentContentDeletion())
        .target(DocumentAttributeTargetConverter.toSdkDocumentAttributeTarget(inlineCDEConfiguration.getTarget()))
        .build();
  }

  public static InlineCustomDocumentEnrichmentConfiguration toModelInlineCDEConfiguration(
      final software.amazon.awssdk.services.kendra.model.InlineCustomDocumentEnrichmentConfiguration inlineCDEConfiguration
  ) {
    if (inlineCDEConfiguration == null) {
      return null;
    }
    return InlineCustomDocumentEnrichmentConfiguration.builder()
        .condition(DocumentAttributeConditionConverter.toModelDocumentAttributeCondition(inlineCDEConfiguration.condition()))
        .documentContentDeletion(inlineCDEConfiguration.documentContentDeletion())
        .target(DocumentAttributeTargetConverter.toModelDocumentAttributeTarget(inlineCDEConfiguration.target()))
        .build();
  }
}
