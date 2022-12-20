package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.Relevance;
import software.amazon.awssdk.services.kendra.model.Search;
import software.amazon.awssdk.services.kendra.model.ListIndicesRequest;
import software.amazon.awssdk.services.kendra.model.ListIndicesResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ServerSideEncryptionConfiguration;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UserTokenConfiguration;
import software.amazon.awssdk.services.kendra.model.JsonTokenTypeConfiguration;
import software.amazon.awssdk.services.kendra.model.JwtTokenTypeConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  static CreateIndexRequest translateToCreateRequest(final ResourceModel model) {
    final CreateIndexRequest.Builder builder = CreateIndexRequest
            .builder()
            .name(model.getName())
            .roleArn(model.getRoleArn())
            .description(model.getDescription())
            .edition(model.getEdition())
            .userTokenConfigurations(translateToSdkUserTokenConfigurations(model.getUserTokenConfigurations()))
            .userContextPolicy(model.getUserContextPolicy());
    builder.tags(ListConverter.toSdk(model.getTags(), x -> Tag.builder().key(x.getKey()).value(x.getValue()).build()));
    if (model.getServerSideEncryptionConfiguration() != null
            && (model.getServerSideEncryptionConfiguration().getKmsKeyId() != null)) {
      builder.serverSideEncryptionConfiguration(
              ServerSideEncryptionConfiguration
                      .builder()
                      .kmsKeyId(model.getServerSideEncryptionConfiguration().getKmsKeyId())
                      .build());
    }
    return builder.build();
  }

  static ListTagsForResourceRequest translateToListTagsRequest(final String arn) {
    return ListTagsForResourceRequest
            .builder()
            .resourceARN(arn)
            .build();
  }

  static UntagResourceRequest translateToUntagResourceRequest(Set<software.amazon.kendra.index.Tag> tags, String arn) {
    return UntagResourceRequest
            .builder()
            .resourceARN(arn)
            .tagKeys(tags.stream().map(x -> x.getKey()).collect(Collectors.toList()))
            .build();
  }

  static TagResourceRequest translateToTagResourceRequest(Set<software.amazon.kendra.index.Tag> tags, String arn) {
    return TagResourceRequest
            .builder()
            .resourceARN(arn)
            .tags(tags.stream().map(x -> Tag
                    .builder()
                    .key(x.getKey())
                    .value(x.getValue()).build())
                    .collect(Collectors.toList()))
            .build();
  }

  static DescribeIndexRequest translateToReadRequest(final ResourceModel model) {
    final DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
            .id(model.getId())
            .build();
    return describeIndexRequest;
  }

  static ResourceModel translateFromReadResponse(final DescribeIndexResponse describeIndexResponse,
                                                 final ListTagsForResourceResponse listTagsForResourceResponse,
                                                 String arn) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
    ResourceModel.ResourceModelBuilder builder = ResourceModel.builder()
            .id(describeIndexResponse.id())
            .arn(arn)
            .name(describeIndexResponse.name())
            .roleArn(describeIndexResponse.roleArn())
            .description(describeIndexResponse.description())
            .edition(describeIndexResponse.edition().toString())
            .userContextPolicy(describeIndexResponse.userContextPolicy() == null ? null : describeIndexResponse.userContextPolicy().toString())
            .userTokenConfigurations(translateFromSdkUserTokenConfigurations(describeIndexResponse.userTokenConfigurations()));
    if (describeIndexResponse.serverSideEncryptionConfiguration() != null
            && (describeIndexResponse.serverSideEncryptionConfiguration().kmsKeyId() != null)) {
      builder.serverSideEncryptionConfiguration(
              software.amazon.kendra.index.ServerSideEncryptionConfiguration
                      .builder()
                      .kmsKeyId(describeIndexResponse.serverSideEncryptionConfiguration().kmsKeyId())
                      .build());
    }
    if (describeIndexResponse.capacityUnits() != null) {
      CapacityUnitsConfiguration capacityUnitsConfiguration = describeIndexResponse.capacityUnits();
      // If VCU is equal to its null equivalent (ie storage and query capacity units
      // are both equal to 0), then don't set VCU in the resource model.
      if (capacityUnitsConfiguration.queryCapacityUnits() != 0
              || capacityUnitsConfiguration.storageCapacityUnits() != 0) {
        builder.capacityUnits(software.amazon.kendra.index.CapacityUnitsConfiguration
                .builder()
                .storageCapacityUnits(describeIndexResponse.capacityUnits().storageCapacityUnits())
                .queryCapacityUnits(describeIndexResponse.capacityUnits().queryCapacityUnits())
                .build());
      }
    }
    List<software.amazon.kendra.index.Tag> tags = ListConverter.toModel(listTagsForResourceResponse.tags(),
            x -> software.amazon.kendra.index.Tag.builder().key(x.key()).value(x.value()).build());
    builder.tags(tags);
    List<software.amazon.kendra.index.DocumentMetadataConfiguration> modelDocumentMetadataConfigurationList =
            translateFromSdkDocumentMetadataConfigurationList(describeIndexResponse.documentMetadataConfigurations());
    builder.documentMetadataConfigurations(modelDocumentMetadataConfigurationList);

    return builder.build();
  }

  static DeleteIndexRequest translateToDeleteRequest(final ResourceModel model) {
    final DeleteIndexRequest deleteIndexRequest = DeleteIndexRequest
            .builder()
            .id(model.getId())
            .build();
    return deleteIndexRequest;
  }

  static UpdateIndexRequest translateToUpdateRequest(final ResourceModel currModel,
                                                     final ResourceModel prevModel) throws TranslatorValidationException {
    // Null equivalents for partial updates.
    String description = currModel.getDescription() == null ? "" : currModel.getDescription();
    String name = currModel.getName() == null ? "" : currModel.getName();
    String roleArn = currModel.getRoleArn() == null ? "" : currModel.getRoleArn();
    // Handle null previous resource model
    List<software.amazon.kendra.index.DocumentMetadataConfiguration> prevDocumentMetadataConfiguration =
            prevModel == null ? new ArrayList<>() : prevModel.getDocumentMetadataConfigurations();

    final UpdateIndexRequest.Builder builder = UpdateIndexRequest
            .builder()
            .id(currModel.getId())
            .roleArn(roleArn)
            .name(name)
            .description(description)
            .documentMetadataConfigurationUpdates(
                    translateToSdkDocumentMetadataConfigurationList(
                            currModel.getDocumentMetadataConfigurations(),
                            prevDocumentMetadataConfiguration))
            .capacityUnits(translateToCapacityUnitsConfiguration(currModel.getCapacityUnits(), currModel.getEdition()))
            .userTokenConfigurations(translateToSdkUserTokenConfigurations(currModel.getUserTokenConfigurations()));
    if (currModel.getUserContextPolicy() != null) {
      builder.userContextPolicy(currModel.getUserContextPolicy());
    }
    return builder.build();
  }

  static CapacityUnitsConfiguration translateToCapacityUnitsConfiguration(
          software.amazon.kendra.index.CapacityUnitsConfiguration modelCapacityUnitsConfiguration,
          String indexEdition) {
    // For developer edition we can't provide CapacityUnitsConfiguration, including it's null
    // equivalent. Thus, return null.
    if (indexEdition.equals(IndexEdition.DEVELOPER_EDITION.toString())) {
      return null;
    }
    if (modelCapacityUnitsConfiguration != null) {
      return CapacityUnitsConfiguration
              .builder()
              .storageCapacityUnits(modelCapacityUnitsConfiguration.getStorageCapacityUnits())
              .queryCapacityUnits(modelCapacityUnitsConfiguration.getQueryCapacityUnits())
              .build();
    } else {
      // Null equivalent.
      return CapacityUnitsConfiguration
              .builder()
              .queryCapacityUnits(0)
              .storageCapacityUnits(0)
              .build();
    }
  }


  static UpdateIndexRequest translateToPostCreateUpdateRequest(final ResourceModel model) throws TranslatorValidationException {
    // We only need to update attributes we couldn't set during create.
    return UpdateIndexRequest
            .builder()
            .id(model.getId())
            .documentMetadataConfigurationUpdates(
                    translateToSdkDocumentMetadataConfigurationList(model.getDocumentMetadataConfigurations()))
            .capacityUnits(translateToCapacityUnitsConfiguration(model.getCapacityUnits(), model.getEdition()))
            .build();
  }

  static List<DocumentMetadataConfiguration> translateToSdkDocumentMetadataConfigurationList(
          List<software.amazon.kendra.index.DocumentMetadataConfiguration> curr,
          List<software.amazon.kendra.index.DocumentMetadataConfiguration> prev) throws TranslatorValidationException {

    Map<String, String> previousMetadataNames = new HashMap<>();
    if (prev != null && !prev.isEmpty()) {
      // duplicated config name is not checked, so some cloudformation template might contain duplicated names
      // use for loop instead of stream since collect to map does not support duplicated key. This prevents
      // any updates for the existing cloudformation templates that has duplicated keys
      for (software.amazon.kendra.index.DocumentMetadataConfiguration documentMetadataConfiguration : prev) {
        previousMetadataNames.put(documentMetadataConfiguration.getName(), documentMetadataConfiguration.getType());
      }
    }
    Set<String> currMetadataNames = new HashSet<>();
    if (curr != null && !curr.isEmpty()) {
      currMetadataNames = curr.stream().map(x -> x.getName()).collect(Collectors.toSet());
    }
    List<DocumentMetadataConfiguration> sdkDefaultAttributes = new ArrayList<>();
    for (Map.Entry<String, String> entry : previousMetadataNames.entrySet()) {
      // If the attribute is a reserved one (i.e. it is prefixed with "_") ...
      if (entry.getKey().startsWith("_")) {
        // and it's not in the requested CloudFormation template,
        // then provide the default value. This allows customers to add and remove
        // reserved attributes. When removed, we set/reset the attribute to it's default
        if (!currMetadataNames.contains(entry.getKey())) {
          sdkDefaultAttributes.add(
                  DocumentMetadataConfiguration
                          .builder()
                          .name(entry.getKey())
                          .type(entry.getValue())
                          .search((Search) null)
                          .relevance((Relevance) null)
                          .build());
        }
      } else {
        // otherwise it's a custom field. We don't allow customers to remove
        // custom fields from their CloudFormation template so check for that here.
        if (!currMetadataNames.contains(entry.getKey())) {
          throw new TranslatorValidationException(
                  String.format("Custom attribute %s cannot be removed", entry.getKey()));
        }
      }
    }

    // Document metadata configuration directly defined/requested in the CloudFormation template
    List<DocumentMetadataConfiguration> sdkAttributesDefinedInCFTemplate =
            translateToSdkDocumentMetadataConfigurationList(curr);

    return Stream.concat(
            sdkAttributesDefinedInCFTemplate.stream(),
            sdkDefaultAttributes.stream())
            .collect(Collectors.toList());

  }

  static List<DocumentMetadataConfiguration> translateToSdkDocumentMetadataConfigurationList(
          List<software.amazon.kendra.index.DocumentMetadataConfiguration> modelDocumentMetadataConfigurationList) throws TranslatorValidationException {

    List<DocumentMetadataConfiguration> sdkDocumentMetadataConfigurationList = new ArrayList<>();
    if (modelDocumentMetadataConfigurationList != null && !modelDocumentMetadataConfigurationList.isEmpty()) {
      sdkDocumentMetadataConfigurationList = new ArrayList<>();
      for (software.amazon.kendra.index.DocumentMetadataConfiguration modelDocumentMetadataConfiguration : modelDocumentMetadataConfigurationList) {
        DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder = DocumentMetadataConfiguration.builder();

        sdkDocumentMetadataConfigurationBuilder.name(modelDocumentMetadataConfiguration.getName());
        sdkDocumentMetadataConfigurationBuilder.type(modelDocumentMetadataConfiguration.getType());

        Relevance sdkRelevance = translateToSdkRelevance(modelDocumentMetadataConfiguration.getRelevance());
        sdkDocumentMetadataConfigurationBuilder.relevance(sdkRelevance);

        Search sdkSearch = translateToSdkSearch(modelDocumentMetadataConfiguration.getSearch());
        sdkDocumentMetadataConfigurationBuilder.search(sdkSearch);

        sdkDocumentMetadataConfigurationList.add(sdkDocumentMetadataConfigurationBuilder.build());
      }
    }
    return sdkDocumentMetadataConfigurationList;
  }

  private static Relevance translateToSdkRelevance(software.amazon.kendra.index.Relevance modelRelevance) throws TranslatorValidationException {
    if (modelRelevance != null) {
      Relevance.Builder sdkRelevanceBuilder = Relevance.builder();
      sdkRelevanceBuilder.freshness(modelRelevance.getFreshness());
      sdkRelevanceBuilder.importance(modelRelevance.getImportance());
      sdkRelevanceBuilder.duration(modelRelevance.getDuration());
      sdkRelevanceBuilder.rankOrder(modelRelevance.getRankOrder());
      if (modelRelevance.getValueImportanceItems() != null) {
        List<String> keys = modelRelevance.getValueImportanceItems().stream().map(x -> x.getKey()).collect(Collectors.toList());
        Set<String> keysDeduplicated = keys.stream().collect(Collectors.toSet());
        if (keys.size() != keysDeduplicated.size()) {
          throw new TranslatorValidationException("ValueImportanceItems can not contain duplicate keys.");
        }
        sdkRelevanceBuilder.valueImportanceMap(modelRelevance.getValueImportanceItems().stream()
                .collect(Collectors.toMap(ValueImportanceItem::getKey, ValueImportanceItem::getValue)));
      }
      return sdkRelevanceBuilder.build();
    } else {
      return null;
    }
  }

  private static Search translateToSdkSearch(software.amazon.kendra.index.Search modelSearch) {
    if (modelSearch != null) {
      Search.Builder sdkSearchBuilder = Search.builder();
      sdkSearchBuilder.displayable(modelSearch.getDisplayable());
      sdkSearchBuilder.facetable(modelSearch.getFacetable());
      sdkSearchBuilder.searchable(modelSearch.getSearchable());
      sdkSearchBuilder.sortable(modelSearch.getSortable());
      return sdkSearchBuilder.build();
    } else {
      return null;
    }
  }


  static List<software.amazon.kendra.index.DocumentMetadataConfiguration> translateFromSdkDocumentMetadataConfigurationList(
          List<DocumentMetadataConfiguration> sdkDocumentMetadataConfigurationList) {
    return ListConverter.toModel(sdkDocumentMetadataConfigurationList, Translator::toModelDocumentMetadataConfiguration);
  }

  static software.amazon.kendra.index.DocumentMetadataConfiguration toModelDocumentMetadataConfiguration(DocumentMetadataConfiguration sdk) {
    if (sdk == null) {
      return null;
    }
    software.amazon.kendra.index.DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder
            model = software.amazon.kendra.index.DocumentMetadataConfiguration.builder();
    model.name(sdk.name());
    model.type(sdk.typeAsString());
    software.amazon.kendra.index.Relevance modelRelevance =
            translateFromSdkRelevance(sdk.relevance());
    if (modelRelevance != null) {
      model.relevance(modelRelevance);
    }
    software.amazon.kendra.index.Search modelSearch = translateFromSdkSearch(sdk.search());
    if (modelSearch != null) {
      model.search(modelSearch);
    }
    return model.build();
  }

  private static software.amazon.kendra.index.Relevance translateFromSdkRelevance(Relevance sdkRelevance) {
    if (sdkRelevance != null) {
      software.amazon.kendra.index.Relevance.RelevanceBuilder modelRelevanceBuilder =
              software.amazon.kendra.index.Relevance.builder();
      modelRelevanceBuilder.importance(sdkRelevance.importance());
      modelRelevanceBuilder.freshness(sdkRelevance.freshness());
      modelRelevanceBuilder.duration(sdkRelevance.duration());
      modelRelevanceBuilder.rankOrder(sdkRelevance.rankOrderAsString());
      modelRelevanceBuilder.valueImportanceItems(sdkRelevance.valueImportanceMap().entrySet()
              .stream().map(entry -> ValueImportanceItem.builder().key(entry.getKey()).value(entry.getValue()).build())
              .collect(Collectors.toList()));
      return modelRelevanceBuilder.build();
    } else {
      return null;
    }
  }

  private static software.amazon.kendra.index.Search translateFromSdkSearch(Search sdkSearch) {
    if (sdkSearch != null) {
      software.amazon.kendra.index.Search.SearchBuilder modelSearchBuilder =
              software.amazon.kendra.index.Search.builder();
      modelSearchBuilder.searchable(sdkSearch.searchable());
      modelSearchBuilder.sortable(sdkSearch.sortable());
      modelSearchBuilder.facetable(sdkSearch.facetable());
      modelSearchBuilder.displayable(sdkSearch.displayable());
      return modelSearchBuilder.build();
    } else {
      return null;
    }
  }

  static ListIndicesRequest translateToListRequest(final String nextToken) {
    final ListIndicesRequest listIndicesRequest = ListIndicesRequest
            .builder()
            .nextToken(nextToken)
            .build();
    return listIndicesRequest;
  }

  static List<ResourceModel> translateFromListResponse(final ListIndicesResponse listIndicesResponse) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
    return streamOfOrEmpty(listIndicesResponse.indexConfigurationSummaryItems())
            .map(resource -> ResourceModel.builder()
                    .id(resource.id())
                    .build())
            .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
  }

  private static List<UserTokenConfiguration> translateToSdkUserTokenConfigurations(List<software.amazon.kendra.index.UserTokenConfiguration> modelUserTokenConfigurations) {
    if (modelUserTokenConfigurations != null && !modelUserTokenConfigurations.isEmpty()) {
      return modelUserTokenConfigurations
          .stream()
          .map(x -> UserTokenConfiguration.builder()
              .jsonTokenTypeConfiguration(translateToSdkJsonTokenTypeConfiguration(x.getJsonTokenTypeConfiguration()))
              .jwtTokenTypeConfiguration(translateToSdkJwtTokenTypeConfiguration(x.getJwtTokenTypeConfiguration()))
              .build())
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  private static JsonTokenTypeConfiguration translateToSdkJsonTokenTypeConfiguration(software.amazon.kendra.index.JsonTokenTypeConfiguration modelJsonTokenTypeConfiguration) {
    if (modelJsonTokenTypeConfiguration != null) {
      JsonTokenTypeConfiguration.Builder sdkJsonTokenTypeConfigurationBuilder = JsonTokenTypeConfiguration.builder();
      sdkJsonTokenTypeConfigurationBuilder.userNameAttributeField(modelJsonTokenTypeConfiguration.getUserNameAttributeField());
      sdkJsonTokenTypeConfigurationBuilder.groupAttributeField(modelJsonTokenTypeConfiguration.getGroupAttributeField());
      return sdkJsonTokenTypeConfigurationBuilder.build();
    } else {
      return null;
    }
  }

  private static JwtTokenTypeConfiguration translateToSdkJwtTokenTypeConfiguration(software.amazon.kendra.index.JwtTokenTypeConfiguration modelJwtTokenTypeConfiguration) {
    if (modelJwtTokenTypeConfiguration != null) {
      JwtTokenTypeConfiguration.Builder sdkJwtTokenTypeConfigurationBuilder = JwtTokenTypeConfiguration.builder();
      sdkJwtTokenTypeConfigurationBuilder.keyLocation(modelJwtTokenTypeConfiguration.getKeyLocation());
      sdkJwtTokenTypeConfigurationBuilder.url(modelJwtTokenTypeConfiguration.getURL());
      sdkJwtTokenTypeConfigurationBuilder.secretManagerArn(modelJwtTokenTypeConfiguration.getSecretManagerArn());
      sdkJwtTokenTypeConfigurationBuilder.issuer(modelJwtTokenTypeConfiguration.getIssuer());
      sdkJwtTokenTypeConfigurationBuilder.claimRegex(modelJwtTokenTypeConfiguration.getClaimRegex());
      sdkJwtTokenTypeConfigurationBuilder.userNameAttributeField(modelJwtTokenTypeConfiguration.getUserNameAttributeField());
      sdkJwtTokenTypeConfigurationBuilder.groupAttributeField(modelJwtTokenTypeConfiguration.getGroupAttributeField());
      return sdkJwtTokenTypeConfigurationBuilder.build();
    } else {
      return null;
    }
  }

  private static List<software.amazon.kendra.index.UserTokenConfiguration> translateFromSdkUserTokenConfigurations(List<UserTokenConfiguration> sdkUserTokenConfigurations) {
    if (sdkUserTokenConfigurations != null && !sdkUserTokenConfigurations.isEmpty()) {
      return sdkUserTokenConfigurations
          .stream()
          .map(x -> software.amazon.kendra.index.UserTokenConfiguration.builder()
              .jsonTokenTypeConfiguration(translateFromSdkJsonTokenTypeConfiguration(x.jsonTokenTypeConfiguration()))
              .jwtTokenTypeConfiguration(translateFromSdkJwtTokenTypeConfiguration(x.jwtTokenTypeConfiguration()))
              .build())
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  private static software.amazon.kendra.index.JsonTokenTypeConfiguration translateFromSdkJsonTokenTypeConfiguration(JsonTokenTypeConfiguration sdkJsonTokenTypeConfiguration) {
    if (sdkJsonTokenTypeConfiguration != null) {
      software.amazon.kendra.index.JsonTokenTypeConfiguration.JsonTokenTypeConfigurationBuilder modelJsonTokenTypeConfigurationBuilder =
          software.amazon.kendra.index.JsonTokenTypeConfiguration.builder();
      modelJsonTokenTypeConfigurationBuilder.userNameAttributeField(sdkJsonTokenTypeConfiguration.userNameAttributeField());
      modelJsonTokenTypeConfigurationBuilder.groupAttributeField(sdkJsonTokenTypeConfiguration.groupAttributeField());
      return modelJsonTokenTypeConfigurationBuilder.build();
    } else {
      return null;
    }
  }

  private static software.amazon.kendra.index.JwtTokenTypeConfiguration translateFromSdkJwtTokenTypeConfiguration(JwtTokenTypeConfiguration sdkJwtTokenTypeConfiguration) {
    if (sdkJwtTokenTypeConfiguration != null) {
      software.amazon.kendra.index.JwtTokenTypeConfiguration.JwtTokenTypeConfigurationBuilder modelJwtTokenTypeConfigurationBuilder =
          software.amazon.kendra.index.JwtTokenTypeConfiguration.builder();
      modelJwtTokenTypeConfigurationBuilder.keyLocation(sdkJwtTokenTypeConfiguration.keyLocation().toString());
      modelJwtTokenTypeConfigurationBuilder.uRL(sdkJwtTokenTypeConfiguration.url());
      modelJwtTokenTypeConfigurationBuilder.secretManagerArn(sdkJwtTokenTypeConfiguration.secretManagerArn());
      modelJwtTokenTypeConfigurationBuilder.issuer(sdkJwtTokenTypeConfiguration.issuer());
      modelJwtTokenTypeConfigurationBuilder.claimRegex(sdkJwtTokenTypeConfiguration.claimRegex());
      modelJwtTokenTypeConfigurationBuilder.userNameAttributeField(sdkJwtTokenTypeConfiguration.userNameAttributeField());
      modelJwtTokenTypeConfigurationBuilder.groupAttributeField(sdkJwtTokenTypeConfiguration.groupAttributeField());
      return modelJwtTokenTypeConfigurationBuilder.build();
    } else {
      return null;
    }
  }
}
