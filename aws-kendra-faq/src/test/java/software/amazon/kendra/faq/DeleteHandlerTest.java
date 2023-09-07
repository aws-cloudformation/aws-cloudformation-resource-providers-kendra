package software.amazon.kendra.faq;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.DeleteFaqRequest;
import software.amazon.awssdk.services.kendra.model.DeleteFaqResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFaqRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFaqResponse;
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
        final DeleteHandler handler = new DeleteHandler();

        String indexId = "indexId";
        String id = "id";
        final ResourceModel model = ResourceModel
                .builder()
                .id(id)
                .indexId(indexId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().deleteFaq(any(DeleteFaqRequest.class)))
                .thenReturn(DeleteFaqResponse.builder().build());
        when(proxyClient.client().describeFaq(any(DescribeFaqRequest.class)))
                .thenReturn(DescribeFaqResponse.builder().build())
                .thenThrow(ResourceNotFoundException.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).deleteFaq(any(DeleteFaqRequest.class));
        verify(proxyClient.client(), times(2)).describeFaq(any(DescribeFaqRequest.class));
    }

    private static Stream<Arguments> testItThrowsExpectedCfnErrorArguments() {
        return Stream.of(
            Arguments.of(AwsServiceException.builder().build(), CfnGeneralServiceException.class),
            Arguments.of(ResourceNotFoundException.builder().build(), CfnNotFoundException.class),
            Arguments.of(ConflictException.builder().build(), CfnResourceConflictException.class),
            Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
            Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
            Arguments.of(ValidationException.builder().build(), CfnInvalidRequestException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("testItThrowsExpectedCfnErrorArguments")
    public void testItThrowsExpectedCfnErrorForDeleteFaqKendraException(
        AwsServiceException kendraSvcException,
        Class<? extends RuntimeException> expectedCfnError
    ) {
        final DeleteHandler handler = new DeleteHandler();
        final ResourceModel model = ResourceModel
                .builder()
                .id("id")
                .indexId("indexId")
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(proxyClient.client().deleteFaq(any(DeleteFaqRequest.class)))
                .thenThrow(kendraSvcException);

        assertThrows(expectedCfnError, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
        verify(proxyClient.client(), times(1)).deleteFaq(any(DeleteFaqRequest.class));
        verify(proxyClient.client(), times(0)).describeFaq(any(DescribeFaqRequest.class));
    }
}
