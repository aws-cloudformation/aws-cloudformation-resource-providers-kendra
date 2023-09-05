package software.amazon.kendra.featuredresultsset;

import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsSetSummary;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsRequest;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
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
import static software.amazon.kendra.featuredresultsset.AbstractTestBase.MOCK_CREDENTIALS;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase{

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
    public void tear_down() {
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler();
        String indexId = "indexId";
        String frsId = "frsId";
        String frsName = "frsName";

        final ResourceModel model = ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetId(frsId)
            .featuredResultsSetName(frsName)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().listFeaturedResultsSets(any(
            ListFeaturedResultsSetsRequest.class))).thenReturn(
            ListFeaturedResultsSetsResponse.builder()
                .featuredResultsSetSummaryItems(Arrays.asList(
                    FeaturedResultsSetSummary.builder()
                        .featuredResultsSetId(frsId)
                        .featuredResultsSetName(frsName)
                        .build()))
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

        verify(proxyClient.client(), times(1)).listFeaturedResultsSets(any(ListFeaturedResultsSetsRequest.class));
    }
}
