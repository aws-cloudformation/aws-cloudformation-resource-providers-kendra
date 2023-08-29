package software.amazon.kendra.datasource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.TagResourceResponse;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceResponse;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
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

    private static final String TEST_ID = "testId";
    private static final String TEST_DATA_SOURCE_NAME = "testDataSource";
    private static final String TEST_INDEX_ID = "testIndexId";
    private static final String TEST_ROLE_ARN = "testRoleArn";
    private static final String TEST_DESCRIPTION = "testDescription";
    private static final String TEST_SCHEDULE = "testSchedule";
    private static final String TEST_DATA_SOURCE_TYPE = "testDataSourceType";
    private static final String PREV_TEST_DATA_SOURCE_TYPE = "prevTestDataSourceType";

    TestDataSourceArnBuilder testDataSourceArnBuilder = new TestDataSourceArnBuilder();

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient awsKendraClient;

    ResourceModel standardResourceModel;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        awsKendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, awsKendraClient);
        standardResourceModel = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .build();
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(awsKendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
             .id(TEST_ID)
                .indexId(TEST_INDEX_ID)
                .name(TEST_DATA_SOURCE_NAME)
                .schedule(TEST_SCHEDULE)
                .roleArn(TEST_ROLE_ARN)
                .description(TEST_DESCRIPTION)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
                .thenReturn(UpdateDataSourceResponse.builder().build());

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
                .thenReturn(DescribeDataSourceResponse.builder()
                                .id(TEST_ID)
                                .indexId(TEST_INDEX_ID)
                                .name(TEST_DATA_SOURCE_NAME)
                                .schedule(TEST_SCHEDULE)
                                .description(TEST_DESCRIPTION)
                                .roleArn(TEST_ROLE_ARN)
                                .type(TEST_DATA_SOURCE_TYPE)
                                .status(DataSourceStatus.ACTIVE)
                                .build(),
                        DescribeDataSourceResponse.builder()
                                .id(TEST_ID)
                                .indexId(TEST_INDEX_ID)
                                .name(TEST_DATA_SOURCE_NAME)
                                .schedule(TEST_SCHEDULE)
                                .description(TEST_DESCRIPTION)
                                .roleArn(TEST_ROLE_ARN)
                                .type(TEST_DATA_SOURCE_TYPE)
                                .status(DataSourceStatus.ACTIVE)
                                .build()
                );
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

        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(3)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();

    }

    @Test
    public void handleRequest_UpdatingToActive() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

         final ResourceModel model = ResourceModel.builder()
             .id(TEST_ID)
             .indexId(TEST_INDEX_ID)
             .name(TEST_DATA_SOURCE_NAME)
             .schedule(TEST_SCHEDULE)
             .roleArn(TEST_ROLE_ARN)
             .description(TEST_DESCRIPTION)
             .build();

         final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
             .desiredResourceState(model)
             .build();

         when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
             .thenReturn(UpdateDataSourceResponse.builder().build());

        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
                .thenReturn(
                        DescribeDataSourceResponse.builder()
                                .id(TEST_ID)
                                .indexId(TEST_INDEX_ID)
                                .name(TEST_DATA_SOURCE_NAME)
                                .schedule(TEST_SCHEDULE)
                                .description(TEST_DESCRIPTION)
                                .roleArn(TEST_ROLE_ARN)
                                .type(TEST_DATA_SOURCE_TYPE)
                                .status(DataSourceStatus.ACTIVE)
                                .build(),
                        DescribeDataSourceResponse.builder()
                                .id(TEST_ID)
                                .indexId(TEST_INDEX_ID)
                                .name(TEST_DATA_SOURCE_NAME)
                                .schedule(TEST_SCHEDULE)
                                .description(TEST_DESCRIPTION)
                                .roleArn(TEST_ROLE_ARN)
                                .type(TEST_DATA_SOURCE_TYPE)
                                .status(DataSourceStatus.UPDATING)
                                .build(),
                        DescribeDataSourceResponse.builder()
                                .id(TEST_ID)
                                .indexId(TEST_INDEX_ID)
                                .name(TEST_DATA_SOURCE_NAME)
                                .schedule(TEST_SCHEDULE)
                                .description(TEST_DESCRIPTION)
                                .roleArn(TEST_ROLE_ARN)
                 .type(TEST_DATA_SOURCE_TYPE)
                 .status(DataSourceStatus.ACTIVE)
                 .build()
                 );

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

        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(4)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void testThatItThrowsCfnThrottlingException() {
        // set up test scenario
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(standardResourceModel)
            .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenThrow(ThrottlingException.builder().build());

        // call and verify error
        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(CfnThrottlingException.class);
        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(0)).listTagsForResource(any(ListTagsForResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void testThatItThrowsCfnServiceLimitExceededException() {
        // set up test scenario
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(standardResourceModel)
            .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenThrow(ServiceQuotaExceededException.builder().build());

        // call and verify error
        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(CfnServiceLimitExceededException.class);
        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(0)).listTagsForResource(any(ListTagsForResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void testThatItThrowsCfnAccessDeniedError() {
        // set up test scenario
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(standardResourceModel)
            .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenThrow(AccessDeniedException.builder().build());

        // call and verify error
        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(CfnAccessDeniedException.class);
        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(0)).listTagsForResource(any(ListTagsForResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_throwsCfnInvalidRequestException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

         when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
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
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_throwsCfnNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

         when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
                 .thenThrow(ResourceNotFoundException.builder().build());

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

         assertThrows(CfnNotFoundException.class, () -> {
             handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
         });
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();

    }

    @Test
    public void handleRequest_throwsCfnResourceConflictException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

         when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
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
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_throwsCfnGeneralServiceException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

         when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
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
        verify(proxyClient.client(), times(1)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_AddNewTags() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);
        String key = "key";
        String value = "value";
        List<Tag> tags = Arrays.asList(Tag.builder().key(key).value(value).build());
        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .tags(tags)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
          .desiredResourceState(model)
          .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenReturn(UpdateDataSourceResponse.builder().build());
        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
           .thenReturn(DescribeDataSourceResponse.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .type(TEST_DATA_SOURCE_TYPE)
            .status(DataSourceStatus.ACTIVE)
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

        ResourceModel expectedResourceModel = ResourceModel.builder()
             .id(TEST_ID)
             .indexId(TEST_INDEX_ID)
             .name(TEST_DATA_SOURCE_NAME)
             .arn(testDataSourceArnBuilder.build(request))
             .description(TEST_DESCRIPTION)
             .roleArn(TEST_ROLE_ARN)
             .schedule(TEST_SCHEDULE)
             .type(TEST_DATA_SOURCE_TYPE)
             .tags(tags)
             .build();

        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(3)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).tagResource(any(TagResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_RemoveTags() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);
        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .build();

        String key = "key";
        String value = "value";
        final ResourceModel prevModel = ResourceModel.builder()
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
          .desiredResourceState(model)
          .previousResourceState(prevModel)
          .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenReturn(UpdateDataSourceResponse.builder().build());
        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
           .thenReturn(DescribeDataSourceResponse.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .type(TEST_DATA_SOURCE_TYPE)
            .status(DataSourceStatus.ACTIVE)
            .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
            .thenReturn(ListTagsForResourceResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
            .thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

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

        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(3)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).untagResource(any(UntagResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_AddAndRemoveTags() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);
        String keyAdd = "keyAdd";
        String valueAdd = "valueAdd";
        List<Tag> tagsToAdd = Arrays.asList(Tag.builder().key(keyAdd).value(valueAdd).build());
        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .tags(tagsToAdd)
            .build();

        String keyRemove = "keyRemove";
        String valueRemove = "valueRemove";
        final ResourceModel prevModel = ResourceModel.builder()
                .id(TEST_ID)
                .tags(Arrays.asList(Tag.builder().key(keyRemove).value(valueRemove).build()))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
          .desiredResourceState(model)
          .previousResourceState(prevModel)
          .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenReturn(UpdateDataSourceResponse.builder().build());
        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
           .thenReturn(DescribeDataSourceResponse.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .type(TEST_DATA_SOURCE_TYPE)
            .status(DataSourceStatus.ACTIVE)
            .build());

        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
            .thenReturn(ListTagsForResourceResponse
                .builder()
                .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                    .builder().key(keyAdd).value(valueAdd).build()))
                .build());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
            .thenReturn(TagResourceResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
            .thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        ResourceModel expectedResourceModel = ResourceModel.builder()
             .id(TEST_ID)
             .indexId(TEST_INDEX_ID)
             .name(TEST_DATA_SOURCE_NAME)
             .arn(testDataSourceArnBuilder.build(request))
             .description(TEST_DESCRIPTION)
             .roleArn(TEST_ROLE_ARN)
             .schedule(TEST_SCHEDULE)
             .type(TEST_DATA_SOURCE_TYPE)
             .tags(tagsToAdd)
             .build();

        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(3)).describeDataSource(any(DescribeDataSourceRequest.class));
        verify(proxyClient.client(), times(1)).listTagsForResource(any(ListTagsForResourceRequest.class));
        verify(proxyClient.client(), times(1)).tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), times(1)).untagResource(any(UntagResourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_TagResourceThrowsException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);
        String key = "key";
        String value = "value";
        List<Tag> tags = Arrays.asList(Tag.builder().key(key).value(value).build());
        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .tags(tags)
            .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenReturn(UpdateDataSourceResponse.builder().build());
        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
           .thenReturn(DescribeDataSourceResponse.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .type(TEST_DATA_SOURCE_TYPE)
            .status(DataSourceStatus.ACTIVE)
            .build());

        when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
                .thenThrow(ValidationException.builder().build());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
          .desiredResourceState(model)
          .build();

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(2)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_UntagResourceThrowsException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .build();

        String key = "key";
        String value = "value";
        final ResourceModel prevModel = ResourceModel.builder()
                .tags(Arrays.asList(Tag.builder().key(key).value(value).build()))
                .build();

        when(proxyClient.client().updateDataSource(any(UpdateDataSourceRequest.class)))
            .thenReturn(UpdateDataSourceResponse.builder().build());
        when(proxyClient.client().describeDataSource(any(DescribeDataSourceRequest.class)))
            .thenReturn(DescribeDataSourceResponse.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .description(TEST_DESCRIPTION)
            .roleArn(TEST_ROLE_ARN)
            .type(TEST_DATA_SOURCE_TYPE)
            .status(DataSourceStatus.ACTIVE)
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

        verify(proxyClient.client(), times(1)).updateDataSource(any(UpdateDataSourceRequest.class));
        verify(proxyClient.client(), times(2)).describeDataSource(any(DescribeDataSourceRequest.class));

        verify(awsKendraClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_FailWith_CfnNotUpdatableException() {
        final UpdateHandler handler = new UpdateHandler(testDataSourceArnBuilder);

        final ResourceModel model = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .type(TEST_DATA_SOURCE_TYPE)
            .build();

        final ResourceModel prevModel = ResourceModel.builder()
            .id(TEST_ID)
            .indexId(TEST_INDEX_ID)
            .name(TEST_DATA_SOURCE_NAME)
            .schedule(TEST_SCHEDULE)
            .roleArn(TEST_ROLE_ARN)
            .description(TEST_DESCRIPTION)
            .type(PREV_TEST_DATA_SOURCE_TYPE)
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
