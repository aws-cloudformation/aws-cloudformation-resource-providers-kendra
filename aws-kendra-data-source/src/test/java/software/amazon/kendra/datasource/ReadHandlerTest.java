package software.amazon.kendra.datasource;

import java.time.Duration;
import java.util.Arrays;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    private static final String TEST_ID = "testId";
    private static final String TEST_DATA_SOURCE_NAME = "testDataSource";
    private static final String TEST_INDEX_ID = "testIndexId";
    private static final String TEST_ROLE_ARN = "testRoleArn";
    private static final String TEST_DESCRIPTION = "testDescription";
    private static final String TEST_SCHEDULE = "testSchedule";
    private static final String TEST_DATA_SOURCE_TYPE = "testDataSourceType";

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient awsKendraClient;

    TestDataSourceArnBuilder testDataSourceArnBuilder = new TestDataSourceArnBuilder();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        awsKendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, awsKendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(testDataSourceArnBuilder);
        final ResourceModel model = ResourceModel.builder()
        .id(TEST_ID)
        .indexId(TEST_INDEX_ID)
        .build();

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
            .thenReturn(DescribeDataSourceResponse.builder()
                .id(TEST_ID)
                .name(TEST_DATA_SOURCE_NAME)
                .indexId(TEST_INDEX_ID)
                .type(TEST_DATA_SOURCE_TYPE)
                .configuration(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder().build())
                .description(TEST_DESCRIPTION)
                .roleArn(TEST_ROLE_ARN)
                .schedule(TEST_SCHEDULE)
                .status(DataSourceStatus.ACTIVE)
                .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse.builder().build());


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        ResourceModel expectedResourceModel = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .arn(testDataSourceArnBuilder.build(request))
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .type(TEST_DATA_SOURCE_TYPE)
            .build();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));
    }

    @Test
    public void handleRequest_WithTags() {
        final ReadHandler handler = new ReadHandler(testDataSourceArnBuilder);
        final ResourceModel model = ResourceModel.builder()
        .id(TEST_ID)
        .indexId(TEST_INDEX_ID)
        .build();

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
            .thenReturn(DescribeDataSourceResponse.builder()
                .id(TEST_ID)
                .name(TEST_DATA_SOURCE_NAME)
                .indexId(TEST_INDEX_ID)
                .type(TEST_DATA_SOURCE_TYPE)
                .configuration(software.amazon.awssdk.services.kendra.model.DataSourceConfiguration.builder().build())
                .description(TEST_DESCRIPTION)
                .roleArn(TEST_ROLE_ARN)
                .schedule(TEST_SCHEDULE)
                .status(DataSourceStatus.ACTIVE)
                .build());

        String key = "key";
        String value = "value";
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                        .build());


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        ResourceModel expectedResourceModel = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .arn(testDataSourceArnBuilder.build(request))
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .type(TEST_DATA_SOURCE_TYPE)
            .tags(Arrays.asList(software.amazon.kendra.datasource.Tag.builder().key(key).value(value).build()))
            .build();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));
    }

    @Test
    public void handleRequest_throws_CfnNotFoundException() {
        final ReadHandler handler = new ReadHandler(testDataSourceArnBuilder);

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
                .thenThrow(ResourceNotFoundException.builder().build());

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_throws_CfnGeneralServiceException() {
        final ReadHandler handler = new ReadHandler(testDataSourceArnBuilder);

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
                .thenThrow(AwsServiceException.builder().build());

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }
}
