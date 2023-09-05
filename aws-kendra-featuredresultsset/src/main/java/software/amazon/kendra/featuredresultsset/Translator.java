package software.amazon.kendra.featuredresultsset;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.FeaturedDocument;
import software.amazon.awssdk.services.kendra.model.FeaturedDocumentWithMetadata;
import software.amazon.awssdk.services.kendra.model.FeaturedDocumentMissing;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsRequest;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
   * @return awsRequest the aws service request to create a resource
   */
  static CreateFeaturedResultsSetRequest translateToCreateRequest(final ResourceModel model) {
    CreateFeaturedResultsSetRequest.Builder builder = CreateFeaturedResultsSetRequest.builder()
        .indexId(model.getIndexId())
        .featuredResultsSetName(model.getFeaturedResultsSetName())
        .queryTexts(model.getQueryTexts())
        .featuredDocuments(translateToSdkFeaturedDocuments(model.getFeaturedDocuments()))
        .status(model.getStatus())
        .description(model.getDescription());
    return builder.build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static DescribeFeaturedResultsSetRequest translateToReadRequest(final ResourceModel model) {
    DescribeFeaturedResultsSetRequest.Builder builder = DescribeFeaturedResultsSetRequest.builder()
        .indexId(model.getIndexId())
        .featuredResultsSetId(model.getFeaturedResultsSetId());
    return builder.build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param awsResponse the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final DescribeFeaturedResultsSetResponse awsResponse, final String frsArn, final String indexId) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
    // TODO: Implement once Tagris integration is complete
    return ResourceModel.builder()
        .indexId(indexId)
        .arn(frsArn)
        .status(awsResponse.statusAsString())
        .featuredResultsSetId(awsResponse.featuredResultsSetId())
        .featuredResultsSetName(awsResponse.featuredResultsSetName())
        .description(awsResponse.description())
        .queryTexts(awsResponse.queryTexts())
        .featuredDocuments(translateFromSdkFeaturedDocuments(awsResponse.featuredDocumentsWithMetadata(),
            awsResponse.featuredDocumentsMissing()))
        .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static BatchDeleteFeaturedResultsSetRequest translateToDeleteRequest(final ResourceModel model) {
    BatchDeleteFeaturedResultsSetRequest.Builder builder = BatchDeleteFeaturedResultsSetRequest.builder()
        .indexId(model.getIndexId())
        .featuredResultsSetIds(Arrays.asList(model.getFeaturedResultsSetId()));
    return builder.build();
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateFeaturedResultsSetRequest translateToUpdateRequest(final ResourceModel model) {
    UpdateFeaturedResultsSetRequest.Builder builder = UpdateFeaturedResultsSetRequest.builder()
        .indexId(model.getIndexId())
        .featuredResultsSetId(model.getFeaturedResultsSetId())
        .featuredResultsSetName(model.getFeaturedResultsSetName())
        .queryTexts(model.getQueryTexts())
        .featuredDocuments(translateToSdkFeaturedDocuments(model.getFeaturedDocuments()))
        .status(model.getStatus())
        .description(model.getDescription());
    return builder.build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListFeaturedResultsSetsRequest translateToListRequest(final ResourceModel model, final String nextToken) {
    ListFeaturedResultsSetsRequest.Builder builder = ListFeaturedResultsSetsRequest.builder()
        .indexId(model.getIndexId())
        .nextToken(nextToken);
    return builder.build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param awsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListFeaturedResultsSetsResponse awsResponse, final String indexId) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
    return streamOfOrEmpty(awsResponse.featuredResultsSetSummaryItems())
        .map(resource -> ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetName(resource.featuredResultsSetName())
            .featuredResultsSetId(resource.featuredResultsSetId())
            .status(resource.statusAsString())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }

  private static List<FeaturedDocument> translateToSdkFeaturedDocuments(
      List<software.amazon.kendra.featuredresultsset.FeaturedDocument> modelFeaturedDocuments) {
    // We don't want to get an empty stream here if null, because they are considered different
    // cases in Featured results. (Null = no changes to docs list. Empty = set has no docs).
    if (modelFeaturedDocuments == null) {
      return null;
    }
    return (modelFeaturedDocuments).stream()
        .map(modelDoc -> FeaturedDocument.builder()
            .id(modelDoc.getId())
            .build())
        .collect(Collectors.toList());
  }

  // Returns model featured documents from the FRS describe response's featured documents with metadata list.
  // Combines with the missing docs as well.
  private static List<software.amazon.kendra.featuredresultsset.FeaturedDocument> translateFromSdkFeaturedDocuments(
      List<FeaturedDocumentWithMetadata> sdkFeaturedDocs,
      List<FeaturedDocumentMissing> sdkMissingDocs) {
    if (sdkFeaturedDocs == null && sdkMissingDocs == null) {
      return null;
    }
    List<software.amazon.kendra.featuredresultsset.FeaturedDocument> modelDocs = new ArrayList<>();
    if (sdkFeaturedDocs != null) {
      modelDocs.addAll((sdkFeaturedDocs).stream()
          .map(sdkDoc -> software.amazon.kendra.featuredresultsset.FeaturedDocument.builder()
              .id(sdkDoc.id())
              .build())
          .collect(Collectors.toList()));
    }
    if (sdkMissingDocs != null) {
      modelDocs.addAll((sdkMissingDocs).stream()
          .map(missingDoc -> software.amazon.kendra.featuredresultsset.FeaturedDocument.builder()
              .id(missingDoc.id())
              .build())
          .collect(Collectors.toList()));
    }
    return modelDocs;
  }
}
