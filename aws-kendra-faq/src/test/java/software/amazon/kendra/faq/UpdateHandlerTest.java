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
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceResponse;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        String fileFormat = "CSV";
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
                .fileFormat(fileFormat)
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
                        .fileFormat(fileFormat)
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
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .arn(faqArnBuilder.build(request))
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
        verify(kendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_RequiredOnlySuccess() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String roleArn = "roleArn";
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
        ResourceModel expectedResourceModel = ResourceModel
            .builder()
            .id(faqId)
            .indexId(indexId)
            .arn(faqArnBuilder.build(request))
            .name(name)
            .roleArn(roleArn)
            .s3Path(s3Path)
            .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
        verify(kendraClient, atLeastOnce()).serviceName();
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
        String fileFormat = "CSV";
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
                .fileFormat(fileFormat)
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
                        .fileFormat(fileFormat)
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
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .arn(faqArnBuilder.build(request))
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
        verify(kendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_RemoveTags() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        String fileFormat = "CSV";
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
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .build();

        String key = "key";
        String value = "value";
        ResourceModel prevModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .previousResourceState(prevModel)
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
                        .fileFormat(fileFormat)
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
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .arn(faqArnBuilder.build(request))
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
        verify(kendraClient, atLeastOnce()).serviceName();
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
        String fileFormat = "CSV";
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
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .tags(Arrays.asList(Tag.builder().key(tagKeyToAdd).value(tagValueToAdd).build()))
                .build();
        String tagKeyToRemove = "keyToRemove";
        String tagValueToRemove = "valueToRemove";
        ResourceModel prevModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .tags(Arrays.asList(Tag.builder().key(tagKeyToRemove).value(tagValueToRemove).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .previousResourceState(prevModel)
                .build();

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
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
                        .fileFormat(fileFormat)
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
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .arn(faqArnBuilder.build(request))
                .tags(Arrays.asList(Tag.builder().key(tagKeyToAdd).value(tagValueToAdd).build()))
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .fileFormat(fileFormat)
                .s3Path(s3Path)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), times(1))
                .tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
        verify(kendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forIndexId() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String indexId = "indexId";
        String prevIndexId = "prevIndexId";
        final ResourceModel model = ResourceModel
                .builder()
                .indexId(indexId)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .indexId(prevIndexId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forName() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String name = "name";
        String prevName = "prevName";
        String indexId = "indexId";
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .indexId(indexId)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .indexId(indexId)
                .name(prevName)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forS3Path() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String indexId = "indexId";
        String s3Key = "s3Key";
        String s3Bucket = "s3Bucket";
        String oldS3Bucket = "oldS3Bucket";
        S3Path s3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(s3Bucket)
                .build();
        S3Path oldS3Path = S3Path
                .builder()
                .key(s3Key)
                .bucket(oldS3Bucket)
                .build();


        final ResourceModel model = ResourceModel
                .builder()
                .s3Path(s3Path)
                .indexId(indexId)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .indexId(indexId)
                .s3Path(oldS3Path)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forDescription() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String indexId = "indexId";
        String description = "description";
        String oldDescription = "oldDescription";

        final ResourceModel model = ResourceModel
                .builder()
                .description(description)
                .indexId(indexId)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .description(oldDescription)
                .indexId(indexId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forRoleArn() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String indexId = "indexId";
        String roleArn = "roleArn";
        String oldRoleArn = "oldRoleArn";

        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .indexId(indexId)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .roleArn(oldRoleArn)
                .indexId(indexId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forFileFormat() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);
        String indexId = "indexId";
        String fileFormat = "JSON";
        String oldFileFormat = "CSV";

        final ResourceModel model = ResourceModel
            .builder()
            .indexId(indexId)
            .fileFormat(fileFormat)
            .build();

        final ResourceModel prevModel = ResourceModel
            .builder()
            .indexId(indexId)
            .fileFormat(oldFileFormat)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .previousResourceState(prevModel)
            .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_ThrowsNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(faqArnBuilder);

        String faqId = "faqId";
        String indexId = "indexId";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(faqId)
                .indexId(indexId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                        .thenThrow(ResourceNotFoundException.builder().build());

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
        verify(kendraClient, atLeastOnce()).serviceName();
    }
}
