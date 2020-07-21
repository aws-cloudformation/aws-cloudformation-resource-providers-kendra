package software.amazon.kendra.faq;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.CreateFaqRequest;
import software.amazon.awssdk.services.kendra.model.CreateFaqResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.FaqStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient kendraClient;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        kendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, kendraClient);
    }

    @AfterEach
    public void post_execute() {
        verify(kendraClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        String indexId = "indexId";
        String roleArn = "roleArn";
        String description = "description";
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
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(s3Path)
                .roleArn(roleArn)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(resourceModel)
            .build();

        String id = "id";
        when(proxyClient.client().createFaq(any(CreateFaqRequest.class)))
                .thenReturn(CreateFaqResponse.builder().id(id).build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(id)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .key(s3Key)
                                .bucket(s3Bucket)
                                .build())
                        .status(FaqStatus.ACTIVE)
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        ResourceModel expectedResourceModel = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .description(description)
                .roleArn(roleArn)
                .name(name)
                .s3Path(software.amazon.kendra.faq.S3Path.builder()
                        .key(s3Key)
                        .bucket(s3Bucket)
                        .build())
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createFaq(any(CreateFaqRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
    }

    @Test
    public void handleRequest_CreatingToActive() {
        final CreateHandler handler = new CreateHandler();

        String indexId = "indexId";
        String roleArn = "roleArn";
        String description = "description";
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
                .indexId(indexId)
                .description(description)
                .name(name)
                .s3Path(s3Path)
                .roleArn(roleArn)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(resourceModel)
                .build();

        String id = "id";
        when(proxyClient.client().createFaq(any(CreateFaqRequest.class)))
                .thenReturn(CreateFaqResponse.builder().id(id).build());

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(id)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
                        .s3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                .builder()
                                .key(s3Key)
                                .bucket(s3Bucket)
                                .build())
                        .status(FaqStatus.CREATING)
                        .build())
                .thenReturn(DescribeFaqResponse
                        .builder()
                        .id(id)
                        .indexId(indexId)
                        .name(name)
                        .roleArn(roleArn)
                        .description(description)
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
                .id(id)
                .indexId(indexId)
                .description(description)
                .roleArn(roleArn)
                .name(name)
                .s3Path(software.amazon.kendra.faq.S3Path.builder()
                        .key(s3Key)
                        .bucket(s3Bucket)
                        .build())
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).createFaq(any(CreateFaqRequest.class));
        verify(proxyClient.client(), times(3)).describeFaq(any(DescribeFaqRequest.class));
    }
}
