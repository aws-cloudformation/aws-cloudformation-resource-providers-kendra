package software.amazon.kendra.datasource;

import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DataSourceType;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceRequest;
import software.amazon.kendra.datasource.convert.DatabaseConverter;
import software.amazon.kendra.datasource.convert.ListConverter;
import software.amazon.kendra.datasource.convert.OneDriveConverter;
import software.amazon.kendra.datasource.convert.S3Converter;
import software.amazon.kendra.datasource.convert.ServiceNowConverter;
import software.amazon.kendra.datasource.convert.SharePointConverter;
import software.amazon.kendra.datasource.convert.SalesforceConverter;


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
   * @return awsRequest the aws service request to create a resource
   */
  static CreateDataSourceRequest translateToCreateRequest(final ResourceModel model) {
    final CreateDataSourceRequest.Builder builder = CreateDataSourceRequest
      .builder()
      .name(model.getName())
      .indexId(model.getIndexId())
      .type(model.getType())
      .configuration(toSdkDataSourceConfiguration(model.getDataSourceConfiguration(), model.getType()))
      .description(model.getDescription())
      .schedule(model.getSchedule())
      .roleArn(model.getRoleArn());
    if (model.getTags() != null && !model.getTags().isEmpty()) {
      builder.tags(model.getTags().stream().map(
              x -> Tag.builder().key(x.getKey()).value(x.getValue()).build())
              .collect(Collectors.toList()));
    }
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
                                                 final ListTagsForResourceResponse listTagsForResourceResponse,
                                                 final String dataSourceArn) {
    ResourceModel.ResourceModelBuilder builder = ResourceModel.builder()
            .id(describeDataSourceResponse.id())
            .arn(dataSourceArn)
            .name(describeDataSourceResponse.name())
            .description(describeDataSourceResponse.description())
            .indexId(describeDataSourceResponse.indexId())
            .roleArn(describeDataSourceResponse.roleArn())
            .schedule(describeDataSourceResponse.schedule())
            .type(describeDataSourceResponse.typeAsString())
            .dataSourceConfiguration(toModelDataSourceConfiguration(describeDataSourceResponse.configuration(),
                    describeDataSourceResponse.typeAsString()));
    List<software.amazon.kendra.datasource.Tag> tags = ListConverter.toModel(
            listTagsForResourceResponse.tags(),
            x -> software.amazon.kendra.datasource.Tag.builder().key(x.key()).value(x.value()).build());
    builder.tags(tags);
    return builder.build();
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
  static UpdateDataSourceRequest translateToUpdateRequest(final ResourceModel model) {
    String description = model.getDescription() == null ? "" : model.getDescription();
    String name = model.getName() == null ? "" : model.getName();
    String roleArn = model.getRoleArn() == null ? "" : model.getRoleArn();
    String schedule = model.getSchedule() == null ? "" : model.getSchedule();
    software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration = model.getDataSourceConfiguration() == null ?
      software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder().build()
      : toSdkDataSourceConfiguration(model.getDataSourceConfiguration(), model.getType());
    final UpdateDataSourceRequest updateDataSourceRequest = UpdateDataSourceRequest.builder()
      .id(model.getId())
      .indexId(model.getIndexId())
      .roleArn(roleArn)
      .name(name)
      .description(description)
      .configuration(dataSourceConfiguration)
      .schedule(schedule)
      .build();
    return updateDataSourceRequest;
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

  static ListTagsForResourceRequest translateToListTagsRequest(final String arn) {
    return ListTagsForResourceRequest
            .builder()
            .resourceARN(arn)
            .build();
  }

  static UntagResourceRequest translateToUntagResourceRequest(Set<software.amazon.kendra.datasource.Tag> tags, String arn) {
    return UntagResourceRequest
            .builder()
            .resourceARN(arn)
            .tagKeys(tags.stream().map(x -> x.getKey()).collect(Collectors.toList()))
            .build();
  }

  static TagResourceRequest translateToTagResourceRequest(Set<software.amazon.kendra.datasource.Tag> tags, String arn) {
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

  static software.amazon.awssdk.services.kendra.model.DataSourceConfiguration toSdkDataSourceConfiguration(
    final DataSourceConfiguration dataSourceConfiguration, final String dataSourceType) {
    if (DataSourceType.S3.toString().equals(dataSourceType)) {
      return S3Converter.toSdkDataSourceConfiguration(dataSourceConfiguration.getS3Configuration());
    } else if (DataSourceType.SHAREPOINT.toString().equals(dataSourceType)) {
      return SharePointConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSharePointConfiguration());
    } else if (DataSourceType.SALESFORCE.toString().equals(dataSourceType)){
      return SalesforceConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getSalesforceConfiguration());
    } else if (DataSourceType.DATABASE.toString().equals(dataSourceType)) {
      return DatabaseConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getDatabaseConfiguration());
    } else if (DataSourceType.SERVICENOW.toString().equals(dataSourceType)) {
      return ServiceNowConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getServiceNowConfiguration());
    } else if (DataSourceType.ONEDRIVE.toString().equals(dataSourceType)) {
      return OneDriveConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getOneDriveConfiguration());
    } else {
      return null;
    }
  }

  static DataSourceConfiguration toModelDataSourceConfiguration(
    final software.amazon.awssdk.services.kendra.model.DataSourceConfiguration dataSourceConfiguration, final String dataSourceType) {
    if (DataSourceType.S3.toString().equals(dataSourceType)) {
      return S3Converter.toModelDataSourceConfiguration(dataSourceConfiguration.s3Configuration());
    } else if(DataSourceType.SHAREPOINT.toString().equals(dataSourceType)) {
      return SharePointConverter.toModelDataSourceConfiguration(dataSourceConfiguration.sharePointConfiguration());
    } else if (DataSourceType.SALESFORCE.toString().equals(dataSourceType)) {
      return SalesforceConverter.toModelDataSourceConfiguration(dataSourceConfiguration.salesforceConfiguration());
    } else if (DataSourceType.DATABASE.toString().equals(dataSourceType)) {
      return DatabaseConverter.toModelDataSourceConfiguration(dataSourceConfiguration.databaseConfiguration());
    } else if (DataSourceType.SERVICENOW.toString().equals(dataSourceType)) {
      return ServiceNowConverter.toModelDataSourceConfiguration(dataSourceConfiguration.serviceNowConfiguration());
    } else if (DataSourceType.ONEDRIVE.toString().equals(dataSourceType)) {
      return OneDriveConverter.toModelDataSourceConfiguration(dataSourceConfiguration.oneDriveConfiguration());
    } else {
      return null;
    }
  }
}
