package software.amazon.kendra.datasource;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
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

    private static final String TEST_ID = "testId";
    private static final String TEST_INDEX_ID = "testIndexId";

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

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
                .thenReturn(DeleteDataSourceResponse.builder().build());

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
                .thenThrow(ResourceNotFoundException.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));

    }

    @Test
    public void handleRequest_DeletingToNotFound() {
        final DeleteHandler handler = new DeleteHandler(testDelay);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
                .thenReturn(DeleteDataSourceResponse.builder().build());

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
            .thenReturn(DescribeDataSourceResponse.builder().status(DataSourceStatus.DELETING).build())
            .thenThrow(ResourceNotFoundException.builder().build());

       final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
        verify(proxyClient.client(), times(2)).describeDataSource(any(DescribeDataSourceRequest.class));
    }

    private static Stream<Arguments> testThatItThrowsExpectedCfnErrorForKendraErrorArguments() {
      return Stream.of(
          Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
          Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
          Arguments.of(ValidationException.builder().build(), CfnInvalidRequestException.class),
          Arguments.of(ConflictException.builder().build(), CfnResourceConflictException.class),
          Arguments.of(AwsServiceException.builder().build(), CfnGeneralServiceException.class),
          Arguments.of(ResourceNotFoundException.builder().build(), CfnNotFoundException.class)
      );
    }

    @ParameterizedTest
    @MethodSource("testThatItThrowsExpectedCfnErrorForKendraErrorArguments")
    public void testThatItThrowsExpectedCfnErrorForKendraError(
        AwsServiceException serviceException,
        Class<? extends RuntimeException> cfnErrorExpected
    ) {
      // set up scenario
      final DeleteHandler handler = new DeleteHandler(testDelay);

      final ResourceModel model = ResourceModel.builder()
          .id(TEST_ID)
          .indexId(TEST_INDEX_ID)
          .build();
      final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
          .desiredResourceState(model)
          .build();
      when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
          .thenThrow(serviceException);

      // call and verify error thrown
      assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
          .isInstanceOf(cfnErrorExpected);
      verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
      verify(proxyClient.client(), times(0)).describeDataSource(any(DescribeDataSourceRequest.class));
    }
}
