package software.amazon.kendra.datasource;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
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
public class CreateHandlerTest extends AbstractTestBase {

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

    @AfterEach
    public void post_execute() {
        verify(awsKendraClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(awsKendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();


        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
            .thenReturn(CreateDataSourceResponse.builder().id(TEST_ID).build());
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

       verify(proxyClient.client(), times(1)).createDataSource(any(CreateDataSourceRequest.class));
       verify(proxyClient.client(), times(2)).describeDataSource(any(DescribeDataSourceRequest.class));
    }

    @Test
    public void handleRequest_SimpleSuccessTransitionsFromCreatingToActive() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
            .thenReturn(CreateDataSourceResponse.builder().id(TEST_ID).build());
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
                .status(DataSourceStatus.CREATING)
                .build(),
                DescribeDataSourceResponse.builder()
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

       verify(proxyClient.client(), times(1)).createDataSource(any(CreateDataSourceRequest.class));
       verify(proxyClient.client(), times(3)).describeDataSource(any(DescribeDataSourceRequest.class));
    }

    @Test
    public void handleRequest_throws_CfnInvalidRequestException() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
                .thenThrow(ValidationException.builder().build());

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_throws_CfnResourceConflictException() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
                .thenThrow(ConflictException.builder().build());

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnResourceConflictException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_throws_GeneralAwsServiceException() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
                .thenThrow(AwsServiceException.builder().build());

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_CreateDataSourceFailedAsynchronously() {
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
            .thenReturn(CreateDataSourceResponse.builder().id(TEST_ID).build());
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
                .status(DataSourceStatus.FAILED)
                .build());

        assertThrows(CfnNotStabilizedException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_Tags() {
        String key = "key";
        String value = "value";
        final CreateHandler handler = new CreateHandler(testDataSourceArnBuilder);
        final ResourceModel model = ResourceModel.builder()
            .name(TEST_DATA_SOURCE_NAME)
            .indexId(TEST_INDEX_ID)
            .type(TEST_DATA_SOURCE_TYPE)
            .dataSourceConfiguration(DataSourceConfiguration.builder().build())
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .schedule(TEST_SCHEDULE)
            .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().createDataSource(any(CreateDataSourceRequest.class)))
            .thenReturn(CreateDataSourceResponse.builder().id(TEST_ID).build());
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
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                                .builder().key(key).value(value).build()))
                        .build());

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
            .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
            .build();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createDataSource(any(CreateDataSourceRequest.class));
        verify(proxyClient.client(), times(2)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));


    }

}
