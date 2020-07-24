package software.amazon.kendra.index;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.CreateIndexResponse;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Delay;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.cloudformation.proxy.delay.Constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient awsKendraClient;

    TestIndexArnBuilder testIndexArnBuilder = new TestIndexArnBuilder();

    Delay testDelay = Constant.of().timeout(Duration.ofMinutes(10)).delay(Duration.ofMillis(1L)).build();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        awsKendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, awsKendraClient);
    }

    @AfterEach
    public void post_execute() {
        verify(awsKendraClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(awsKendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String description = "description";
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .description(description)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .description(description)
                        .build());
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .description(description)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createIndex(any(CreateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
    }

    @Test
    public void handleRequest_SimpleSuccessTransitionsFromCreatingToActive() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                                .id(id)
                                .name(name)
                                .roleArn(roleArn)
                                .edition(indexEdition)
                                .status(IndexStatus.CREATING.toString())
                                .build(),
                        DescribeIndexResponse.builder()
                                .id(id)
                                .name(name)
                                .roleArn(roleArn)
                                .edition(indexEdition)
                                .status(IndexStatus.ACTIVE)
                                .build());
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());


        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createIndex(any(CreateIndexRequest.class));
        verify(proxyClient.client(), times(4)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
    }

    @Test
    public void handleRequest_FailWith_InvalidRoleArn() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
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
    }

    @Test
    public void handleRequest_FailWith_GeneralAwsServiceException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenThrow(AwsServiceException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .name("name")
                .roleArn("role")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_CreateIndexFailedAsynchronously() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.FAILED.toString())
                        .build());

        assertThrows(CfnNotStabilizedException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_ConflictException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenThrow(ConflictException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .name("name")
                .roleArn("role")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnResourceConflictException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_Tags() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String key = "key";
        String value = "value";
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
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
                                .builder().key(key).value(value).build()))
                        .build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createIndex(any(CreateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
    }

    @Test
    public void handleRequest_ServerSideEncryption() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String kmsKeyId = "kmsKeyId";
        ServerSideEncryptionConfiguration serverSideEncryptionConfiguration =
                ServerSideEncryptionConfiguration.builder().kmsKeyId(kmsKeyId).build();
        final ResourceModel model = ResourceModel
                .builder()
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .serverSideEncryptionConfiguration(
                                software.amazon.awssdk.services.kendra.model.ServerSideEncryptionConfiguration
                                        .builder()
                                        .kmsKeyId(kmsKeyId)
                                        .build())
                        .build());
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenReturn(UpdateIndexResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createIndex(any(CreateIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).updateIndex(any(UpdateIndexRequest.class));
    }

    @Test
    public void handleRequest_PostCreateUpdateIndexThrowsValidationException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenThrow(ValidationException.builder().build());

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_PostCreateUpdateIndexThrowsGeneralServiceException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenThrow(AwsServiceException.builder().build());

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_FailWith_QuoteException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenThrow(ServiceQuotaExceededException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .name("name")
                .roleArn("role")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceLimitExceededException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_PostCreateUpdateIndexThrowsQuotaException() {
        final CreateHandler handler = new CreateHandler(testIndexArnBuilder, testDelay);

        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .edition(indexEdition)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String id = "testId";
        when(proxyClient.client().createIndex(any(CreateIndexRequest.class)))
                .thenReturn(CreateIndexResponse.builder().id(id).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .build());
        when(proxyClient.client().updateIndex(any(UpdateIndexRequest.class)))
                .thenThrow(ServiceQuotaExceededException.builder().build());

        assertThrows(CfnServiceLimitExceededException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }
}
