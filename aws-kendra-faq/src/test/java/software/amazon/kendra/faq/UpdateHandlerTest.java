package software.amazon.kendra.faq;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.FaqStatus;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceResponse;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {
    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient kendraClient;

    FaqArnBuilder faqArnBuilder = new TestFaqArnBuilder();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        kendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, kendraClient);
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String roleArn = "roleArn";
        String description = "description";
        String name = "name";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(s3Path)
                .roleArn(roleArn)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(faqId)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .key(s3Key)
                                .bucket(s3Bucket)
                                .build())
                        .status(FaqStatus.ACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(resourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(2))
                .listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }

    @Test
    public void handleRequest_AddTags() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String key = "key";
        String value = "value";
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .s3Path(s3Path)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build())
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(software.amazon.awssdk.services.kendra.model.Tag
                                .builder()
                                .key(key)
                                .value(value)
                                .build())
                        .build());

        when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
                .thenReturn(TagResourceResponse.builder().build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(faqId)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .bucket(s3Bucket)
                                .key(s3Key)
                                .build())
                        .status(FaqStatus.ACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(resourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(2))
                .listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }

    @Test
    public void handleRequest_RemoveTags() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String key = "key";
        String value = "value";
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .s3Path(s3Path)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(software.amazon.awssdk.services.kendra.model.Tag
                                .builder()
                                .key(key)
                                .value(value)
                                .build())
                        .build())
                .thenReturn(ListTagsForResourceResponse.builder().build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(faqId)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .bucket(s3Bucket)
                                .key(s3Key)
                                .build())
                        .status(FaqStatus.ACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(resourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(2))
                .listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }

    @Test
    public void handleRequest_AddAndRemoveTags() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        String tagKeyToAdd = "keyToAdd";
        String tagValueToAdd = "valueToAdd";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .s3Path(s3Path)
                .tags(Arrays.asList(Tag.builder().key(tagKeyToAdd).value(tagValueToAdd).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        String tagKeyToRemove = "keyToRemove";
        String tagValueToRemove = "valueToRemove";
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(software.amazon.awssdk.services.kendra.model.Tag
                                .builder()
                                .key(tagKeyToRemove)
                                .value(tagValueToRemove)
                                .build())
                        .build())
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(software.amazon.awssdk.services.kendra.model.Tag
                                .builder()
                                .key(tagKeyToAdd)
                                .value(tagValueToAdd)
                                .build())
                        .build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(faqId)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .bucket(s3Bucket)
                                .key(s3Key)
                                .build())
                        .status(FaqStatus.ACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(resourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(2))
                .listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }

}
