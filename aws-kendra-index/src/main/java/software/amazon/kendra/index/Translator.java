package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.ListIndicesRequest;
import software.amazon.awssdk.services.kendra.model.ListIndicesResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;

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
            .edition(model.getEdition());
    if (model.getTags() != null && !model.getTags().isEmpty()) {
      builder.tags(model.getTags().stream().map(
              x -> Tag.builder().key(x.getKey()).value(x.getValue()).build())
              .collect(Collectors.toList()));
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
            .tags(tags.stream().map(x -> Tag.builder().key(x.getKey()).value(x.getKey()).build()).collect(Collectors.toList()))
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
    List<software.amazon.kendra.index.Tag> tags = null;
    if (listTagsForResourceResponse.tags() != null && !listTagsForResourceResponse.tags().isEmpty()) {
      tags = listTagsForResourceResponse.tags().stream()
              .map(x -> software.amazon.kendra.index.Tag.builder().key(x.key()).value(x.value()).build())
              .collect(Collectors.toList());
    }
    return ResourceModel.builder()
            .id(describeIndexResponse.id())
            .arn(arn)
            .name(describeIndexResponse.name())
            .roleArn(describeIndexResponse.roleArn())
            .edition(describeIndexResponse.edition().toString())
            .tags(tags)
            .build();
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
  static UpdateIndexRequest translateToFirstUpdateRequest(final ResourceModel model) {
    final UpdateIndexRequest updateIndexRequest = UpdateIndexRequest
            .builder()
            .id(model.getId())
            .roleArn(model.getRoleArn())
            .name(model.getName())
            .build();
    return updateIndexRequest;
  }

  /**
   * Request to update some other properties that could not be provisioned through first update request
   * @param model resource model
   * @return updateIndexRequest the aws service request to modify a resource
   */
  static UpdateIndexRequest translateToSecondUpdateRequest(final ResourceModel model) {
    final UpdateIndexRequest updateIndexRequest = UpdateIndexRequest.builder()
            .id(model.getId())
            .name(model.getName())
            .roleArn(model.getRoleArn())
            .build();
    return updateIndexRequest;
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
