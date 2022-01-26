package software.amazon.kendra.datasource.convert.cde;

import java.util.stream.Collectors;

import software.amazon.kendra.datasource.CustomDocumentEnrichmentConfiguration;
import software.amazon.kendra.datasource.convert.ListConverter;

public class CustomDocumentEnrichmentConfigurationConverter {

  public static software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration toSdkCustomDocumentEnrichmentConfiguration(
      final CustomDocumentEnrichmentConfiguration customDocumentEnrichmentConfiguration) {
    if (customDocumentEnrichmentConfiguration == null) {
      return null;
    }
    return software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration.builder()
        .inlineConfigurations(ListConverter.toSdk(customDocumentEnrichmentConfiguration.getInlineConfigurations(),
            InlineCustomDocumentEnrichmentConfigurationConverter::toSdkInlineCDEConfiguration))
        .preExtractionHookConfiguration(HookConfigurationConverter.toSdkHookConfiguration(customDocumentEnrichmentConfiguration.getPreExtractionHookConfiguration()))
        .postExtractionHookConfiguration(HookConfigurationConverter.toSdkHookConfiguration(customDocumentEnrichmentConfiguration.getPostExtractionHookConfiguration()))
        .roleArn(customDocumentEnrichmentConfiguration.getRoleArn())
        .build();
  }

  public static CustomDocumentEnrichmentConfiguration toModelCustomDocumentEnrichmentConfiguration(
      final software.amazon.awssdk.services.kendra.model.CustomDocumentEnrichmentConfiguration customDocumentEnrichmentConfiguration) {
    if (customDocumentEnrichmentConfiguration == null) {
      return null;
    }
    return CustomDocumentEnrichmentConfiguration.builder()
        .inlineConfigurations(ListConverter.toModel(customDocumentEnrichmentConfiguration.inlineConfigurations(),
            InlineCustomDocumentEnrichmentConfigurationConverter::toModelInlineCDEConfiguration))
        .preExtractionHookConfiguration(HookConfigurationConverter.toModelHookConfiguration(customDocumentEnrichmentConfiguration.preExtractionHookConfiguration()))
        .postExtractionHookConfiguration(HookConfigurationConverter.toModelHookConfiguration(customDocumentEnrichmentConfiguration.postExtractionHookConfiguration()))
        .roleArn(customDocumentEnrichmentConfiguration.roleArn())
        .build();
  }

}
