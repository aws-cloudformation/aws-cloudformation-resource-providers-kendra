package software.amazon.kendra.index;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceResponse;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceResponse;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Delay;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.cloudformation.proxy.delay.Constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    KendraClient sdkClient;

    TestIndexArnBuilder testIndexArnBuilder = new TestIndexArnBuilder();

    Delay testDelay = Constant.of().timeout(Duration.ofMinutes(1)).delay(Duration.ofMillis(1L)).build();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .name(name)
                .id(id)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags((java.util.Collection<software.amazon.awssdk.services.kendra.model.Tag>) null)
                        .build());
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        final ResourceModel expectedModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .edition(indexEdition)
                .roleArn(roleArn)
                .name(name)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_UpdatingToActive() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .name(name)
                .id(id)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(
                        DescribeIndexResponse.builder()
                                .id(id)
                                .name(name)
                                .roleArn(roleArn)
                                .edition(indexEdition)
                                .status(IndexStatus.ACTIVE.toString())
                                .build(),
                        DescribeIndexResponse.builder()
                                .id(id)
                                .name(name)
                                .roleArn(roleArn)
                                .edition(indexEdition)
                                .status(IndexStatus.UPDATING.toString())
                                .build(),
                        DescribeIndexResponse
                                .builder()
                                .id(id)
                                .name(name)
                                .roleArn(roleArn)
                                .edition(indexEdition)
                                .status(IndexStatus.ACTIVE.toString())
                                .build());
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        final ResourceModel expectedModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .edition(indexEdition)
                .roleArn(roleArn)
                .name(name)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(4)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_InvalidRoleArn() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder().build());

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenThrow(ValidationException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .name("name")
                .roleArn("role")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    private static Stream<Arguments> updateIndexTestErrorArgs() {
        return Stream.of(
          Arguments.of(ConflictException.builder().build(), CfnResourceConflictException.class),
          Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
          Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
          Arguments.of(ServiceQuotaExceededException.builder().build(), CfnServiceLimitExceededException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("updateIndexTestErrorArgs")
    public void testItThrowsExpectedErrorForKendraException(KendraException kendraException, Class<? extends RuntimeException> cfnError) {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse
                        .builder()
                        .build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenThrow(kendraException);

        final ResourceModel model = ResourceModel
                .builder()
                .name("name")
                .roleArn("role")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(cfnError);
        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_AddNewTags() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String key = "key";
        String value = "value";
        List<Tag> tags = Arrays.asList(Tag.builder().key(key).value(value).build());
        IndexEdition indexEdition = IndexEdition.ENTERPRISE_EDITION;
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .roleArn(roleArn)
                .name(name)
                .tags(tags)
                .edition(indexEdition.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition.toString())
                        .status(IndexStatus.ACTIVE.toString())
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                                .builder().key(key).value(value).build()))
                        .build());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
                .thenReturn(TagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expectedModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .edition(indexEdition.toString())
                .roleArn(roleArn)
                .name(name)
                .tags(tags)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).tagResource(any(TagResourceRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_RemoveTags() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .roleArn(roleArn)
                .name(name)
                .edition(indexEdition)
                .build();

        String key = "key";
        String value = "value";
        final ResourceModel prevModel = ResourceModel
                .builder()
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
                .thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expectedModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .edition(indexEdition)
                .roleArn(roleArn)
                .name(name)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).untagResource(any(UntagResourceRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_AddAndRemoveTags() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String keyAdd = "keyAdd";
        String valueAdd = "valueAdd";
        List<Tag> tagsToAdd = Arrays.asList(Tag.builder().key(keyAdd).value(valueAdd).build());
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .roleArn(roleArn)
                .name(name)
                .tags(tagsToAdd)
                .edition(indexEdition)
                .build();

        String keyRemove = "keyRemove";
        String valueRemove = "valueRemove";
        final ResourceModel prevModel = ResourceModel
                .builder()
                .edition(indexEdition)
                .tags(Arrays.asList(Tag.builder().key(keyRemove).value(valueRemove).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                                .builder().key(keyAdd).value(valueAdd).build()))
                        .build());

        when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
                .thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expectedModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .edition(indexEdition)
                .roleArn(roleArn)
                .name(name)
                .tags(tagsToAdd)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(1)).untagResource(any(UntagResourceRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_TagResourceThrowsException() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String key = "key";
        String value = "value";
        List<Tag> tags = Arrays.asList(Tag.builder().key(key).value(value).build());
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .roleArn(roleArn)
                .name(name)
                .tags(tags)
                .edition(indexEdition)
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());

        when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
                .thenThrow(ValidationException.builder().build());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(2)).describeIndex(any(DescribeIndexRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_UntagResourceThrowsException() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .roleArn(roleArn)
                .name(name)
                .edition(indexEdition)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .edition(indexEdition)
                .tags(Arrays.asList(Tag.builder().key("key").value("value").build()))
                .build();

        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());

        when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
                .thenThrow(ValidationException.builder().build());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
        verify(proxyClient.client(), times(2)).describeIndex(any(DescribeIndexRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_NotFound() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenThrow(ResourceNotFoundException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException_forEdition() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);

        final ResourceModel model = ResourceModel
                .builder()
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .edition("Invalid")
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
    public void handleRequest_FailWith_CfnNotUpdatableException_forServerSideEncryptionConfiguration() {
        final UpdateHandler handler = new UpdateHandler(testIndexArnBuilder, testDelay);
        final ServerSideEncryptionConfiguration serverSideEncryptionConfiguration = ServerSideEncryptionConfiguration.builder()
            .kmsKeyId("kmsKeyId")
            .build();

        final ServerSideEncryptionConfiguration oldServerSideEncryptionConfiguration = ServerSideEncryptionConfiguration.builder()
            .kmsKeyId("oldKmsKeyId")
            .build();

        final ResourceModel model = ResourceModel
                .builder()
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();

        final ResourceModel prevModel = ResourceModel
                .builder()
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .serverSideEncryptionConfiguration(oldServerSideEncryptionConfiguration)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(prevModel)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }
}
