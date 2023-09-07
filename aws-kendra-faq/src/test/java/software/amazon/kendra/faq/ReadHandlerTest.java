package software.amazon.kendra.faq;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.S3Path;
import software.amazon.awssdk.services.kendra.model.Tag;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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
    KendraClient kendraClient;

    FaqArnBuilder faqArnBuilder = new TestFaqArnBuilder();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        kendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, kendraClient);
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(faqArnBuilder);

        String id = "id";
        String indexId = "indexId";
        final ResourceModel model = ResourceModel
                .builder()
                .indexId(indexId)
                .id(id)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String key = "key";
        String bucket = "bucket";
        S3Path s3Path = S3Path.builder()
                .key(key)
                .bucket(bucket)
                .build();
        String roleArn = "roleArn";
        String description = "description";
        String name = "name";
        String fileFormat = "CSV";
        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse.builder()
                        .id(id)
                        .indexId(indexId)
                        .fileFormat(fileFormat)
                        .s3Path(s3Path)
                        .description(description)
                        .name(name)
                        .roleArn(roleArn)
                        .build());

        String tagKey = "key";
        String tagValue = "value";
        when(proxyClient.client().listTagsForResource(any(ListTagsForResourceRequest.class)))
                .thenReturn(ListTagsForResourceResponse
                        .builder()
                        .tags(Arrays.asList(Tag.builder().key(tagKey).value(tagValue).build()))
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
                .arn(faqArnBuilder.build(request))
                .description(description)
                .roleArn(roleArn)
                .name(name)
                .fileFormat(fileFormat)
                .s3Path(software.amazon.kendra.faq.S3Path.builder()
                        .key(tagKey)
                        .bucket(bucket)
                        .build())
                .tags(Arrays.asList(software.amazon.kendra.faq.Tag
                        .builder().key(tagKey)
                        .value(tagValue)
                        .build()))
                .build();
        assertThat(response.getResourceModel()).isEqualTo(expectedResourceModel);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }

    private static Stream<Arguments> testItThrowsExpectedCfnErrorArguments() {
        return Stream.of(
          Arguments.of(AwsServiceException.builder().build(), CfnGeneralServiceException.class),
          Arguments.of(ValidationException.builder().build(), CfnInvalidRequestException.class),
          Arguments.of(ResourceNotFoundException.builder().build(), CfnNotFoundException.class),
          Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
          Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("testItThrowsExpectedCfnErrorArguments")
    public void testItThrowsExpectedCfnErrorForKendraDescribeIndexException(
        AwsServiceException kendraServiceException,
        Class<? extends RuntimeException> expectedCfnException
    ) {
        final ReadHandler handler = new ReadHandler(faqArnBuilder);
        final ResourceModel model = ResourceModel
                .builder()
                .indexId("indexId")
                .id("id")
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenThrow(kendraServiceException);

        assertThrows(expectedCfnException, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
        verify(proxyClient.client(), times(1)).describeFaq(any(DescribeFaqRequest.class));
    }
}
