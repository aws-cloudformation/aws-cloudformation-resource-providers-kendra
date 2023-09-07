package software.amazon.kendra.index;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceInUseException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.UserContextPolicy;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient awsKendraClient;

    TestIndexArnBuilder testIndexArnBuilder = new TestIndexArnBuilder();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        awsKendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, awsKendraClient);
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(awsKendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(testIndexArnBuilder);

        String id = "testId";
        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.ATTRIBUTE_FILTER.toString();

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .userContextPolicy(userContextPolicy)
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expected = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .userContextPolicy(userContextPolicy)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expected);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
    }

    @Test
    public void handleRequest_Tags() {
        final ReadHandler handler = new ReadHandler(testIndexArnBuilder);

        String id = "testId";
        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.ATTRIBUTE_FILTER.toString();

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .userContextPolicy(userContextPolicy)
                        .build());

        String key = "key";
        String value = "value";
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                        .build());

        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expected = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .tags(Arrays.asList(software.amazon.kendra.index.Tag.builder().key(key).value(value).build()))
                .userContextPolicy(userContextPolicy)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expected);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
    }

    private static Stream<Arguments> testItThrowsExpectedCfnErrorForKendraErrorArguments() {
        return Stream.of(
            Arguments.of(KendraException.builder().build(), CfnGeneralServiceException.class),
            Arguments.of(ResourceNotFoundException.builder().build(), CfnNotFoundException.class),
            Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
            Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("testItThrowsExpectedCfnErrorForKendraErrorArguments")
    public void testItThrowsExpectedCfnErrorForKendraError(KendraException kendraException, Class<? extends RuntimeException> expectedCfnError) {
        final ReadHandler handler = new ReadHandler(testIndexArnBuilder);
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenThrow(kendraException);

        final ResourceModel model = ResourceModel
                .builder()
                .id("id")
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(expectedCfnError);

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
    }

    @Test
    public void handleRequest_HandlesResourceInUseException() {
        final ReadHandler handler = new ReadHandler(testIndexArnBuilder);

        String id = "testId";
        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.ATTRIBUTE_FILTER.toString();

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .userContextPolicy(userContextPolicy)
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenThrow(ResourceInUseException.builder().build());

        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
    }

    @Test
    public void handleRequest_DocumentMetadata() {
        final ReadHandler handler = new ReadHandler(testIndexArnBuilder);

        String id = "testId";
        String name = "testName";
        String roleArn = "testRoleArn";
        String indexEdition = IndexEdition.ENTERPRISE_EDITION.toString();
        String documentMetadataConfigurationName = "documentMetadataConfigurationName";
        String documentMetadataConfigurationType = "documentMetadataConfigurationType";
        String userContextPolicy = UserContextPolicy.ATTRIBUTE_FILTER.toString();

        List<software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration> sdkDocumentMetadataConfigurationList =
                Arrays.asList(software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration
                        .builder().name(documentMetadataConfigurationName).type(documentMetadataConfigurationType).build());
        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder()
                        .id(id)
                        .name(name)
                        .roleArn(roleArn)
                        .edition(indexEdition)
                        .status(IndexStatus.ACTIVE.toString())
                        .documentMetadataConfigurations(sdkDocumentMetadataConfigurationList)
                        .userContextPolicy(userContextPolicy)
                        .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());

        List<DocumentMetadataConfiguration> documentMetadataConfigurationList =
                Arrays.asList(DocumentMetadataConfiguration
                        .builder()
                        .name(documentMetadataConfigurationName)
                        .type(documentMetadataConfigurationType)
                        .build());

        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        final ResourceModel expected = ResourceModel
                .builder()
                .id(id)
                .arn(testIndexArnBuilder.build(request))
                .name(name)
                .roleArn(roleArn)
                .edition(indexEdition)
                .documentMetadataConfigurations(documentMetadataConfigurationList)
                .userContextPolicy(userContextPolicy)
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expected);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeIndex(any(DescribeIndexRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
    }
}
