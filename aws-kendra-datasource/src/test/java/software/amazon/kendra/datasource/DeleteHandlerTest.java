package software.amazon.kendra.datasource;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DeleteDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
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

    @Test
    public void handleRequest_throwsCfnNotFoundException() {
       final DeleteHandler handler = new DeleteHandler(testDelay);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
            .thenThrow(ResourceNotFoundException.builder().build());

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
    }

    @Test
    public void handleRequest_throwsCfnResourceConflictException() {
       final DeleteHandler handler = new DeleteHandler(testDelay);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
            .thenThrow(ConflictException.builder().build());

        assertThrows(CfnResourceConflictException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
    }

    @Test
    public void handleRequest_throwsCfnGeneralServiceException() {
       final DeleteHandler handler = new DeleteHandler(testDelay);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().deleteDataSource(any(DeleteDataSourceRequest.class)))
            .thenThrow(AwsServiceException.builder().build());

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).deleteDataSource(any(DeleteDataSourceRequest.class));
    }
}
