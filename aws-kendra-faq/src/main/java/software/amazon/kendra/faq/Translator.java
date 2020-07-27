package software.amazon.kendra.faq;

import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.DeleteFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.ListFaqsRequest;
import software.amazon.awssdk.services.kendra.model.ListFaqsResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.S3Path;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;

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

  static CreateFaqRequest translateToCreateRequest(final ResourceModel model) {
    CreateFaqRequest.Builder builder = CreateFaqRequest.builder()
            .indexId(model.getIndexId())
            .description(model.getDescription())
            .name(model.getName())
            .roleArn(model.getRoleArn())
            .s3Path(S3Path.builder()
                    .key(model.getS3Path().getKey())
                    .bucket(model.getS3Path().getBucket())
                    .build());
    return builder.build();
  }

  static DescribeFaqRequest translateToReadRequest(final ResourceModel model) {
    return DescribeFaqRequest
            .builder()
            .id(model.getId())
            .indexId(model.getIndexId())
            .build();
  }

  static ResourceModel translateFromReadResponse(final DescribeFaqResponse describeFaqResponse,
                                                 final ListTagsForResourceResponse listTagsForResourceResponse) {
    ResourceModel.ResourceModelBuilder builder = ResourceModel.builder()
            .id(describeFaqResponse.id())
            .indexId(describeFaqResponse.indexId())
            .description(describeFaqResponse.description())
            .name(describeFaqResponse.name())
            .roleArn(describeFaqResponse.roleArn())
            .s3Path(software.amazon.kendra.faq.S3Path.builder()
                    .key(describeFaqResponse.s3Path().key())
                    .bucket(describeFaqResponse.s3Path().bucket())
                    .build());
    if (listTagsForResourceResponse.tags() != null
            && !listTagsForResourceResponse.tags().isEmpty()) {
      builder.tags(listTagsForResourceResponse.tags()
              .stream().map(x -> software.amazon.kendra.faq.Tag
                      .builder()
                      .key(x.key())
                      .value(x.value())
                      .build()).collect(Collectors.toList()));
    }
    return builder.build();
  }

  static DeleteFaqRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteFaqRequest
            .builder()
            .id(model.getId())
            .indexId(model.getIndexId())
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListFaqsRequest translateToListRequest(final ResourceModel resourceModel, final String nextToken) {
    return ListFaqsRequest
            .builder()
            .indexId(resourceModel.getIndexId())
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param awsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListFaqsResponse listFaqsResponse, String indexId) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
    return streamOfOrEmpty(listFaqsResponse.faqSummaryItems())
            .map(summary -> ResourceModel.builder()
                    .id(summary.id())
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

  static UntagResourceRequest translateToUntagResourceRequest(Set<software.amazon.kendra.faq.Tag> tags, String arn) {
    return UntagResourceRequest
            .builder()
            .resourceARN(arn)
            .tagKeys(tags.stream().map(x -> x.getKey()).collect(Collectors.toList()))
            .build();
  }

  static TagResourceRequest translateToTagResourceRequest(Set<software.amazon.kendra.faq.Tag> tags, String arn) {
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

}
