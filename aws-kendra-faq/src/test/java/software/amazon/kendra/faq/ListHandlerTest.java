package software.amazon.kendra.faq;

import org.junit.jupiter.api.AfterEach;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ConflictException;
import software.amazon.awssdk.services.kendra.model.FaqStatus;
import software.amazon.awssdk.services.kendra.model.FaqSummary;
import software.amazon.awssdk.services.kendra.model.ListFaqsRequest;
import software.amazon.awssdk.services.kendra.model.ListFaqsResponse;
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

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

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
public class ListHandlerTest extends AbstractTestBase {

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
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler();

        String indexId = "indexId";
        String id = "id";
        final ResourceModel model = ResourceModel
                .builder()
                .indexId(indexId)
                .id(id)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        String name = "name";
        FaqSummary faqSummary = FaqSummary
                .builder()
                .id(id)
                .name(name)
                .status(FaqStatus.ACTIVE)
                .build();
        when(proxyClient.client().listFaqs(any(ListFaqsRequest.class)))
                .thenReturn(ListFaqsResponse
                        .builder()
                        .faqSummaryItems(Arrays.asList(faqSummary))
                        .build());

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyClient.client(), times(1)).listFaqs(any(ListFaqsRequest.class));
    }

    private static Stream<Arguments> testThatItThrowsExpectedCfnErrorForListFaqKendraCallExceptionArguments() {
        return Stream.of(
            Arguments.of(ValidationException.builder().build(), CfnInvalidRequestException.class),
            Arguments.of(ConflictException.builder().build(), CfnResourceConflictException.class),
            Arguments.of(ResourceNotFoundException.builder().build(), CfnNotFoundException.class),
            Arguments.of(ThrottlingException.builder().build(), CfnThrottlingException.class),
            Arguments.of(AccessDeniedException.builder().build(), CfnAccessDeniedException.class),
            Arguments.of(AwsServiceException.builder().build(), CfnGeneralServiceException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("testThatItThrowsExpectedCfnErrorForListFaqKendraCallExceptionArguments")
    public void testThatItThrowsExpectedCfnErrorForListFaqKendraCallException(
        AwsServiceException kendraException,
        Class<? extends RuntimeException> expectedCfnError
    ) {
        // set up test scenario
        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel
            .builder()
            .indexId("indexId")
            .id("id")
            .build();
        when(proxyClient.client().listFaqs(any(ListFaqsRequest.class)))
            .thenThrow(kendraException);

        // call and verify error
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
        assertThatThrownBy(() -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isInstanceOf(expectedCfnError);
        verify(proxyClient.client(), times(1)).listFaqs(any(ListFaqsRequest.class));
    }
}
