package software.amazon.kendra.index;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexResponse;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient sdkClient;

    Delay testDelay = Constant.of().timeout(Duration.ofMinutes(1)).delay(Duration.ofMillis(1L)).build();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void post_execute() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DeleteHandler handler = new DeleteHandler(testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        IndexEdition indexEdition = IndexEdition.ENTERPRISE_EDITION;
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .name(name)
                .edition(indexEdition.toString())
                .id(id)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder().status(IndexStatus.ACTIVE).build())
                .thenThrow(ResourceNotFoundException.builder().build());

        when(proxyClient.client().deleteIndex(any(DeleteIndexRequest.class)))
                .thenReturn(DeleteIndexResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).deleteIndex(any(DeleteIndexRequest.class));
        verify(proxyClient.client(), times(2)).describeIndex(any(DescribeIndexRequest.class));
    }

    @Test
    public void handleRequest_DeletingToNotFound() {
        final DeleteHandler handler = new DeleteHandler(testDelay);

        String roleArn = "roleArn";
        String name = "name";
        String id = "id";
        IndexEdition indexEdition = IndexEdition.ENTERPRISE_EDITION;
        final ResourceModel model = ResourceModel
                .builder()
                .roleArn(roleArn)
                .name(name)
                .edition(indexEdition.toString())
                .id(id)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
                .thenReturn(DescribeIndexResponse.builder().status(IndexStatus.ACTIVE).build())
                .thenReturn(DescribeIndexResponse
                        .builder()
                        .status(IndexStatus.DELETING)
                        .build())
                .thenThrow(ResourceNotFoundException.builder().build());

        when(proxyClient.client().deleteIndex(any(DeleteIndexRequest.class)))
                .thenReturn(DeleteIndexResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).deleteIndex(any(DeleteIndexRequest.class));
        verify(proxyClient.client(), times(3)).describeIndex(any(DescribeIndexRequest.class));
    }

    private static Stream<Arguments> kendraUpdateIndexExceptionTestArgs() {
        return Stream.of(
          Arguments.of(ConflictException.builder().build(), CfnResourceConflictException.class),
          Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
          Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
          Arguments.of(AwsServiceException.builder().build(), CfnGeneralServiceException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("kendraUpdateIndexExceptionTestArgs")
    public void handleRequest_FailWith_ConflictException(AwsServiceException kendraException, Class<? extends RuntimeException> cfnError) {
        final DeleteHandler handler = new DeleteHandler(testDelay);

        when(proxyClient.client().describeIndex(any(DescribeIndexRequest.class)))
               .thenReturn(DescribeIndexResponse.builder().build());

        when(proxyClient.client().deleteIndex(any(DeleteIndexRequest.class)))
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
        verify(proxyClient.client(), times(1)).deleteIndex(any(DeleteIndexRequest.class));
    }
}
