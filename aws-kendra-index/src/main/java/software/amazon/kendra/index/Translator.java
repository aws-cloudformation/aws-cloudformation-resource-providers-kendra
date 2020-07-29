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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

  /**
   * Request to create a resource
   * @param model resource model
   * @return createIndexRequest the aws service request to create a resource
   */
  static CreateIndexRequest translateToCreateRequest(final ResourceModel model) {
    final CreateIndexRequest.Builder builder = CreateIndexRequest
            .builder()
            .name(model.getName())
            .roleArn(model.getRoleArn())
            .description(model.getDescription())
            .edition(model.getEdition());
    if (model.getTags() != null && !model.getTags().isEmpty()) {
      builder.tags(model.getTags().stream().map(
              x -> Tag.builder().key(x.getKey()).value(x.getValue()).build())
              .collect(Collectors.toList()));
    }
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

  /**
   * Request to read a resource
   * @param model resource model
   * @return describeIndexRequest the aws service request to describe a resource
   */
  static DescribeIndexRequest translateToReadRequest(final ResourceModel model) {
    final DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder()
            .id(model.getId())
            .build();
    return describeIndexRequest;
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param describeIndexResponse the aws service describe resource response
   * @return model resource model
   */
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
            .edition(describeIndexResponse.edition().toString());
    if (describeIndexResponse.serverSideEncryptionConfiguration() != null
            && (describeIndexResponse.serverSideEncryptionConfiguration().kmsKeyId() != null)) {
      builder.serverSideEncryptionConfiguration(
              software.amazon.kendra.index.ServerSideEncryptionConfiguration
                      .builder()
                      .kmsKeyId(describeIndexResponse.serverSideEncryptionConfiguration().kmsKeyId())
                      .build());
    }
    if (describeIndexResponse.capacityUnits() != null) {
      builder.capacityUnits(software.amazon.kendra.index.CapacityUnitsConfiguration
              .builder()
              .storageCapacityUnits(describeIndexResponse.capacityUnits().storageCapacityUnits())
              .queryCapacityUnits(describeIndexResponse.capacityUnits().queryCapacityUnits())
              .build());
    }
    if (listTagsForResourceResponse.tags() != null && !listTagsForResourceResponse.tags().isEmpty()) {
      List<software.amazon.kendra.index.Tag> tags = listTagsForResourceResponse.tags().stream()
              .map(x -> software.amazon.kendra.index.Tag.builder().key(x.key()).value(x.value()).build())
              .collect(Collectors.toList());
      builder.tags(tags);
    }
    List<software.amazon.kendra.index.DocumentMetadataConfiguration> modelDocumentMetadataConfigurationList =
            translateFromSdkDocumentMetadataConfigurationList(describeIndexResponse.documentMetadataConfigurations());
    if (!modelDocumentMetadataConfigurationList.isEmpty()) {
      builder.documentMetadataConfigurations(modelDocumentMetadataConfigurationList);
    }

    return builder.build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return deleteIndexRequest the aws service request to delete a resource
   */
  static DeleteIndexRequest translateToDeleteRequest(final ResourceModel model) {
    final DeleteIndexRequest deleteIndexRequest = DeleteIndexRequest
            .builder()
            .id(model.getId())
            .build();
    return deleteIndexRequest;
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return updateIndexRequest the aws service request to modify a resource
   */
  static UpdateIndexRequest translateToUpdateRequest(final ResourceModel model) {
    // Null equivalents for partial updates.
    String description = model.getDescription() == null ? "" : model.getDescription();
    String name = model.getName() == null ? "" : model.getName();
    String roleArn = model.getRoleArn() == null ? "" : model.getRoleArn();
    return UpdateIndexRequest
            .builder()
            .id(model.getId())
            .roleArn(roleArn)
            .name(name)
            .description(description)
            .documentMetadataConfigurationUpdates(translateToSdkDocumentMetadataConfigurationList(model.getDocumentMetadataConfigurations()))
            .capacityUnits(translateToCapacityUnitsConfiguration(model.getCapacityUnits(), model.getEdition()))
            .build();
  }

  static CapacityUnitsConfiguration translateToCapacityUnitsConfiguration(
          software.amazon.kendra.index.CapacityUnitsConfiguration modelCapacityUnitsConfiguration,
          String indexEdition) {
    if (modelCapacityUnitsConfiguration != null) {
      return CapacityUnitsConfiguration
              .builder()
              .storageCapacityUnits(modelCapacityUnitsConfiguration.getStorageCapacityUnits())
              .queryCapacityUnits(modelCapacityUnitsConfiguration.getQueryCapacityUnits())
              .build();
    } else {
      // If the edition type is enterprise, then provide the null equivalent. But if the edition is developer,
      // then provide null - this is because for developer editions we can't provide CapacityUnitsConfiguration
      // without getting a validation exception.
      if (indexEdition.equals(IndexEdition.ENTERPRISE_EDITION.toString())) {
        // Null equivalent for partial updates.
        return CapacityUnitsConfiguration
                .builder()
                .queryCapacityUnits(0)
                .storageCapacityUnits(0)
                .build();
      } else {
        return null;
      }
    }
  }

  /**
   * Request to update some other properties that could not be provisioned through first update request
   * @param model resource model
   * @return updateIndexRequest the aws service request to modify a resource
   */
  static UpdateIndexRequest translateToPostCreateUpdateRequest(final ResourceModel model) {
    // We only need to update attributes we couldn't set during create.
    return UpdateIndexRequest
            .builder()
            .id(model.getId())
            .documentMetadataConfigurationUpdates(
                    translateToSdkDocumentMetadataConfigurationList(model.getDocumentMetadataConfigurations()))
            .capacityUnits(translateToCapacityUnitsConfiguration(model.getCapacityUnits(), model.getEdition()))
            .build();
  }

  static List<DocumentMetadataConfiguration> translateToSdkDocumentMetadataConfigurationList(List<software.amazon.kendra.index.DocumentMetadataConfiguration> modelDocumentMetadataConfigurationList) {
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

  private static Relevance translateToSdkRelevance(software.amazon.kendra.index.Relevance modelRelevance) {
    if (modelRelevance != null) {
      Relevance.Builder sdkRelevanceBuilder = Relevance.builder();
      sdkRelevanceBuilder.freshness(modelRelevance.getFreshness());
      sdkRelevanceBuilder.importance(modelRelevance.getImportance());
      sdkRelevanceBuilder.duration(modelRelevance.getDuration());
      sdkRelevanceBuilder.rankOrder(modelRelevance.getRankOrder());
      if (modelRelevance.getValueImportanceItems() != null) {
        sdkRelevanceBuilder.valueImportanceMap(modelRelevance.getValueImportanceItems().stream()
                .collect(Collectors.toMap(ValueImportanceItem::getKey, ValueImportanceItem::getValue)));
      }
      return sdkRelevanceBuilder.build();
    } else {
      // Null equivalent.
      return Relevance.builder().build();
    }
  }

  private static Search translateToSdkSearch(software.amazon.kendra.index.Search modelSearch) {
    if (modelSearch != null) {
      Search.Builder sdkSearchBuilder = Search.builder();
      sdkSearchBuilder.displayable(modelSearch.getDisplayable());
      sdkSearchBuilder.facetable(modelSearch.getFacetable());
      sdkSearchBuilder.searchable(modelSearch.getSearchable());
      return sdkSearchBuilder.build();
    } else {
      // Null equivalent.
      return Search.builder().build();
    }
  }


  static List<software.amazon.kendra.index.DocumentMetadataConfiguration> translateFromSdkDocumentMetadataConfigurationList(
          List<DocumentMetadataConfiguration> sdkDocumentMetadataConfigurationList) {
    List<software.amazon.kendra.index.DocumentMetadataConfiguration> modelDocumentMetadataConfigurationList =
            new ArrayList<>();
    if (sdkDocumentMetadataConfigurationList != null && !sdkDocumentMetadataConfigurationList.isEmpty()) {
      modelDocumentMetadataConfigurationList = new ArrayList<>();
      for (DocumentMetadataConfiguration sdkDocumentMetadataConfiguration : sdkDocumentMetadataConfigurationList) {
        software.amazon.kendra.index.DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder
                modelDocumentMetadataConfigurationBuilder = software.amazon.kendra.index.DocumentMetadataConfiguration.builder();
        modelDocumentMetadataConfigurationBuilder.name(sdkDocumentMetadataConfiguration.name());
        modelDocumentMetadataConfigurationBuilder.type(sdkDocumentMetadataConfiguration.typeAsString());
        software.amazon.kendra.index.Relevance modelRelevance =
                translateFromSdkRelevance(sdkDocumentMetadataConfiguration.relevance());
        if (modelRelevance != null) {
          modelDocumentMetadataConfigurationBuilder.relevance(modelRelevance);
        }
        software.amazon.kendra.index.Search modelSearch = translateFromSdkSearch(sdkDocumentMetadataConfiguration.search());
        if (modelSearch != null) {
          modelDocumentMetadataConfigurationBuilder.search(modelSearch);
        }
        modelDocumentMetadataConfigurationList.add(modelDocumentMetadataConfigurationBuilder.build());
      }
    }
    return modelDocumentMetadataConfigurationList;
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
      modelSearchBuilder.facetable(sdkSearch.facetable());
      modelSearchBuilder.displayable(sdkSearch.displayable());
      return modelSearchBuilder.build();
    } else {
      return null;
    }
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return listIndicesRequest the aws service request to list resources within aws account
   */
  static ListIndicesRequest translateToListRequest(final String nextToken) {
    final ListIndicesRequest listIndicesRequest = ListIndicesRequest
            .builder()
            .nextToken(nextToken)
            .build();
    return listIndicesRequest;
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param listIndicesResponse the aws service describe resource response
   * @return list of resource models
   */
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
}
