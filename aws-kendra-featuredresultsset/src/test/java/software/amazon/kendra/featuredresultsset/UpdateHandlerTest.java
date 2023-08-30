package software.amazon.kendra.featuredresultsset;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.FeaturedDocumentWithMetadata;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsConflictException;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsSet;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<KendraClient> proxyClient;

    @Mock
    KendraClient kendraClient;

    FeaturedResultsArnBuilder frsArnBuilder = new TestFeaturedResultsArn();

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        kendraClient = mock(KendraClient.class);
        proxyClient = MOCK_PROXY(proxy, kendraClient);
    }

    @AfterEach
    public void tear_down() {
        verify(kendraClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(kendraClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(frsArnBuilder);
        String indexId = "indexId";
        String frsId = "frsId";
        String frsArn = "arn:aws:kendra:us-west-2:0123456789:index/indexId/featured-results-set/frsId";
        String frsName = "frsName";
        String description = "description";
        String status = "ACTIVE";
        List<String> queryTexts = Arrays.asList("query1", "query2");
        List<FeaturedDocument> modelDocs = Arrays.asList(FeaturedDocument.builder().id("doc1").build());
        List<software.amazon.awssdk.services.kendra.model.FeaturedDocument> sdkDocs = Arrays.asList(
            software.amazon.awssdk.services.kendra.model.FeaturedDocument.builder().id("doc1").build());
        List<FeaturedDocumentWithMetadata> sdkDocsWithMetadata = Arrays.asList(
            FeaturedDocumentWithMetadata.builder().id("doc1").build());
        final ResourceModel model = ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetId(frsId)
            .featuredResultsSetName(frsName)
            .queryTexts(queryTexts)
            .featuredDocuments(modelDocs)
            .status(status)
            .description(description)
            .arn(frsArn)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().updateFeaturedResultsSet(any(UpdateFeaturedResultsSetRequest.class)))
            .thenReturn(UpdateFeaturedResultsSetResponse.builder()
                .featuredResultsSet(FeaturedResultsSet.builder()
                    .featuredResultsSetName(frsName)
                    .featuredResultsSetId(frsId)
                    .description(description)
                    .status(status)
                    .queryTexts(queryTexts)
                    .featuredDocuments(sdkDocs)
                    .build())
                .build());

        when(proxyClient.client().describeFeaturedResultsSet(any(DescribeFeaturedResultsSetRequest.class)))
            .thenReturn(DescribeFeaturedResultsSetResponse.builder()
                .featuredResultsSetName(frsName)
                .featuredResultsSetId(frsId)
                .description(description)
                .status(status)
                .queryTexts(queryTexts)
                .featuredDocumentsWithMetadata(sdkDocsWithMetadata)
                .build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ValidationException() {
        final UpdateHandler handler = new UpdateHandler(frsArnBuilder);
        String frsId = "frsId";
        String indexId = "indexId";
        String frsName = "frsName";
        final ResourceModel model = ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetId(frsId)
            .featuredResultsSetName(frsName)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().updateFeaturedResultsSet(any(UpdateFeaturedResultsSetRequest.class)))
            .thenThrow(ValidationException.builder().build());

        when(proxyClient.client().describeFeaturedResultsSet(any(DescribeFeaturedResultsSetRequest.class)))
            .thenReturn(DescribeFeaturedResultsSetResponse.builder()
                .featuredResultsSetName(frsName)
                .build());

        assertThrows(CfnInvalidRequestException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_ConflictException() {
        final UpdateHandler handler = new UpdateHandler(frsArnBuilder);
        String frsId = "frsId";
        String indexId = "indexId";
        String frsName = "frsName";
        final ResourceModel model = ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetId(frsId)
            .featuredResultsSetName(frsName)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().updateFeaturedResultsSet(any(UpdateFeaturedResultsSetRequest.class)))
            .thenThrow(FeaturedResultsConflictException.builder().build());

        when(proxyClient.client().describeFeaturedResultsSet(any(DescribeFeaturedResultsSetRequest.class)))
            .thenReturn(DescribeFeaturedResultsSetResponse.builder()
                .featuredResultsSetName(frsName)
                .build());

        assertThrows(CfnResourceConflictException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }

    @Test
    public void handleRequest_ServiceException() {
        final UpdateHandler handler = new UpdateHandler(frsArnBuilder);
        String frsId = "frsId";
        String indexId = "indexId";
        String frsName = "frsName";
        final ResourceModel model = ResourceModel.builder()
            .indexId(indexId)
            .featuredResultsSetId(frsId)
            .featuredResultsSetName(frsName)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.client().updateFeaturedResultsSet(any(UpdateFeaturedResultsSetRequest.class)))
            .thenThrow(AwsServiceException.builder().build());

        when(proxyClient.client().describeFeaturedResultsSet(any(DescribeFeaturedResultsSetRequest.class)))
            .thenReturn(DescribeFeaturedResultsSetResponse.builder()
                .featuredResultsSetName(frsName)
                .build());

        assertThrows(CfnGeneralServiceException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });
    }
}
