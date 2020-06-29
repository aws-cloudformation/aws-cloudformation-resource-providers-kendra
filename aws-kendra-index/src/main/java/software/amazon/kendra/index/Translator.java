package software.amazon.kendra.index;

import com.google.common.collect.Lists;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.ListIndicesRequest;
import software.amazon.awssdk.services.kendra.model.ListIndicesResponse;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;

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
   * @return createIndexRequest the aws service request to create a resource
   */
  static CreateIndexRequest translateToCreateRequest(final ResourceModel model) {
    final CreateIndexRequest createIndexRequest = CreateIndexRequest
            .builder()
            .name(model.getName())
            .roleArn(model.getRoleArn())
            .edition(model.getEdition())
            .build();
    return createIndexRequest;
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
  static ResourceModel translateFromReadResponse(final DescribeIndexResponse describeIndexResponse) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
    return ResourceModel.builder()
            .id(describeIndexResponse.id())
            .name(describeIndexResponse.name())
            .roleArn(describeIndexResponse.roleArn())
            .edition(describeIndexResponse.edition().toString())
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
