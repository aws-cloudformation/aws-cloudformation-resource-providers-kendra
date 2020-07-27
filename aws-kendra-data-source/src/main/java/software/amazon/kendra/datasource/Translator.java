package software.amazon.kendra.datasource;

import com.google.common.collect.Lists;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesResponse;


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
  static CreateDataSourceRequest translateToCreateRequest(final ResourceModel model) {
    final CreateDataSourceRequest.Builder builder = CreateDataSourceRequest
      .builder()
      .name(model.getName())
      .indexId(model.getIndexId())
      .type(model.getType())
      .configuration(DataSourceRequestConverter.getDataSourceConfiguration(model.getDataSourceConfiguration(), model.getType()))
      .description(model.getDescription())
      .schedule(model.getSchedule())
      .roleArn(model.getRoleArn());
    return builder.build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static DescribeDataSourceRequest translateToReadRequest(final ResourceModel model) {
    final DescribeDataSourceRequest describeDataSourceRequest = DescribeDataSourceRequest.builder()
      .id(model.getId())
      .indexId(model.getIndexId())
      .build();
    return describeDataSourceRequest;
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param describeDataSourceResponse the aws service describe resource response
   * @param dataSourceArn the Arn associated with the data source
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final DescribeDataSourceResponse describeDataSourceResponse,
   final String dataSourceArn) {
    return ResourceModel.builder()
        .id(describeDataSourceResponse.id())
        .arn(dataSourceArn)
        .name(describeDataSourceResponse.name())
        .description(describeDataSourceResponse.description())
        .indexId(describeDataSourceResponse.indexId())
        .roleArn(describeDataSourceResponse.roleArn())
        .schedule(describeDataSourceResponse.schedule())
        .type(describeDataSourceResponse.typeAsString())
        .dataSourceConfiguration(DataSourceResponseConverter.getDataSourceConfiguration(describeDataSourceResponse.configuration(),
          describeDataSourceResponse.typeAsString()))
        .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteDataSourceRequest translateToDeleteRequest(final ResourceModel model) {
    final DeleteDataSourceRequest deleteDataSourceRequest = DeleteDataSourceRequest.builder()
      .id(model.getId())
      .indexId(model.getIndexId())
      .build();
    return deleteDataSourceRequest;
  }

  /**
   * Request to update properties of a previously created resource
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static AwsRequest translateToFirstUpdateRequest(final ResourceModel model) {
    final AwsRequest awsRequest = null;
    // TODO: construct a request
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L45-L50
    return awsRequest;
  }

  /**
   * Request to update some other properties that could not be provisioned through first update request
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static AwsRequest translateToSecondUpdateRequest(final ResourceModel model) {
    final AwsRequest awsRequest = null;
    // TODO: construct a request
    return awsRequest;
  }

  /**
   * Request to list resources
   * @param indexId IndexId assoicated with the request
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListDataSourcesRequest translateToListRequest(final String indexId, final String nextToken) {
    final ListDataSourcesRequest listDataSourcesRequest = ListDataSourcesRequest.builder()
      .indexId(indexId)
      .nextToken(nextToken)
      .build();
    return listDataSourcesRequest;
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param listDataSourcesResponse the aws service describe resource response
   * @param indexId IndexId associated with the Data source
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListDataSourcesResponse listDataSourcesResponse,
    final String indexId) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
    return streamOfOrEmpty(listDataSourcesResponse.summaryItems())
        .map(resource -> ResourceModel.builder()
            .id(resource.id())
            .indexId(indexId)
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
