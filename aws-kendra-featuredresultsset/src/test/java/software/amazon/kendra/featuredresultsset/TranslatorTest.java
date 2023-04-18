package software.amazon.kendra.featuredresultsset;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.BatchDeleteFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.CreateFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.FeaturedDocumentMissing;
import software.amazon.awssdk.services.kendra.model.FeaturedDocumentWithMetadata;
import software.amazon.awssdk.services.kendra.model.FeaturedResultsSetSummary;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsRequest;
import software.amazon.awssdk.services.kendra.model.ListFeaturedResultsSetsResponse;
import software.amazon.awssdk.services.kendra.model.UpdateFeaturedResultsSetRequest;

public class TranslatorTest {

  @Test
  void testTranslateToCreateRequest() {
    String indexId = "indexId";
    String frsName = "frsName";
    String description = "description";
    String status = "ACTIVE";
    List<String> queryTexts = Arrays.asList("query1", "query2");
    List<FeaturedDocument> modelDocs = Arrays.asList(FeaturedDocument.builder().id("doc1").build());
    List<software.amazon.awssdk.services.kendra.model.FeaturedDocument> sdkDocs = Arrays.asList(
        software.amazon.awssdk.services.kendra.model.FeaturedDocument.builder().id("doc1").build());
    final ResourceModel model = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetName(frsName)
        .queryTexts(queryTexts)
        .featuredDocuments(modelDocs)
        .status(status)
        .description(description)
        .build();
    CreateFeaturedResultsSetRequest request = Translator.translateToCreateRequest(model);
    assertThat(request.indexId()).isEqualTo(indexId);
    assertThat(request.featuredResultsSetName()).isEqualTo(frsName);
    assertThat(request.description()).isEqualTo(description);
    assertThat(request.statusAsString()).isEqualTo(status);
    assertThat(request.queryTexts()).isEqualTo(queryTexts);
    assertThat(request.featuredDocuments()).isEqualTo(sdkDocs);
  }

  @Test
  void testTranslateFromReadResponse() {
    String indexId = "indexId";
    String frsId = "frsId";
    String frsArn = "arn:aws:kendra:us-west-2:0123456789:index/indexId/featured-results-set/frsId";
    String frsName = "frsName";
    String description = "description";
    String status = "ACTIVE";
    List<String> queryTexts = Arrays.asList("query1", "query2");
    List<FeaturedDocument> modelDocs = Arrays.asList(FeaturedDocument.builder().id("doc1").build(),
        FeaturedDocument.builder().id("doc2").build());
    List<FeaturedDocumentWithMetadata> sdkDocsWithMetadata = Arrays.asList(
        FeaturedDocumentWithMetadata.builder().id("doc1").build());
    List<FeaturedDocumentMissing> sdkMissingDocs = Arrays.asList(
        FeaturedDocumentMissing.builder().id("doc2").build());

    final DescribeFeaturedResultsSetResponse response = DescribeFeaturedResultsSetResponse.builder()
        .featuredResultsSetName(frsName)
        .featuredResultsSetId(frsId)
        .description(description)
        .status(status)
        .queryTexts(queryTexts)
        .featuredDocumentsWithMetadata(sdkDocsWithMetadata)
        .featuredDocumentsMissing(sdkMissingDocs)
        .build();
    ResourceModel model = Translator.translateFromReadResponse(response, frsArn, indexId);
    assertThat(model.getIndexId()).isEqualTo(indexId);
    assertThat(model.getArn()).isEqualTo(frsArn);
    assertThat(model.getFeaturedResultsSetId()).isEqualTo(frsId);
    assertThat(model.getFeaturedResultsSetName()).isEqualTo(frsName);
    assertThat(model.getDescription()).isEqualTo(description);
    assertThat(model.getStatus()).isEqualTo(status);
    assertThat(model.getQueryTexts()).isEqualTo(queryTexts);
    assertThat(model.getFeaturedDocuments()).isEqualTo(modelDocs);
  }

  @Test
  void testTranslateToReadRequest() {
    String indexId = "indexId";
    String frsId = "frsId";
    final ResourceModel model = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetId(frsId)
        .build();
    DescribeFeaturedResultsSetRequest request = Translator.translateToReadRequest(model);
    assertThat(request.indexId()).isEqualTo(indexId);
    assertThat(request.featuredResultsSetId()).isEqualTo(frsId);
  }

  @Test
  void testTranslateToDeleteRequest() {
    String indexId = "indexId";
    String frsId = "frsId";
    final ResourceModel model = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetId(frsId)
        .build();
    BatchDeleteFeaturedResultsSetRequest request = Translator.translateToDeleteRequest(model);
    assertThat(request.indexId()).isEqualTo(indexId);
    assertThat(request.featuredResultsSetIds()).isEqualTo(Arrays.asList(frsId));
  }

  @Test
  void testTranslateToUpdateRequest() {
    String indexId = "indexId";
    String frsId = "frsId";
    String frsName = "frsName";
    String description = "description";
    String status = "ACTIVE";
    List<String> queryTexts = Arrays.asList("query1", "query2");
    List<FeaturedDocument> modelDocs = Arrays.asList(FeaturedDocument.builder().id("doc1").build());
    List<software.amazon.awssdk.services.kendra.model.FeaturedDocument> sdkDocs = Arrays.asList(
        software.amazon.awssdk.services.kendra.model.FeaturedDocument.builder().id("doc1").build());
    final ResourceModel model = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetId(frsId)
        .featuredResultsSetName(frsName)
        .queryTexts(queryTexts)
        .featuredDocuments(modelDocs)
        .status(status)
        .description(description)
        .build();
    UpdateFeaturedResultsSetRequest request = Translator.translateToUpdateRequest(model);
    assertThat(request.indexId()).isEqualTo(indexId);
    assertThat(request.featuredResultsSetId()).isEqualTo(frsId);
    assertThat(request.featuredResultsSetName()).isEqualTo(frsName);
    assertThat(request.description()).isEqualTo(description);
    assertThat(request.statusAsString()).isEqualTo(status);
    assertThat(request.queryTexts()).isEqualTo(queryTexts);
    assertThat(request.featuredDocuments()).isEqualTo(sdkDocs);
  }

  @Test
  void testTranslateToListRequest() {
    String indexId = "indexId";
    String frsId = "frsId";
    String nextToken = "";
    final ResourceModel model = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetId(frsId)
        .build();
    ListFeaturedResultsSetsRequest request = Translator.translateToListRequest(model, nextToken);
    assertThat(request.indexId()).isEqualTo(indexId);
    assertThat(request.nextToken()).isEqualTo(nextToken);
  }

  @Test
  void testTranslateFromListResponse() {
    String indexId = "indexId";
    String frsId = "frsId";
    String frsName = "frsName";
    String status = "ACTIVE";
    final ListFeaturedResultsSetsResponse response = ListFeaturedResultsSetsResponse.builder()
        .featuredResultsSetSummaryItems(Arrays.asList(
            FeaturedResultsSetSummary.builder()
                .featuredResultsSetName(frsName)
                .featuredResultsSetId(frsId)
                .status(status)
                .build())
        )
        .build();
    final ResourceModel expectedModel = ResourceModel.builder()
        .indexId(indexId)
        .featuredResultsSetId(frsId)
        .featuredResultsSetName(frsName)
        .status(status)
        .build();
    List<ResourceModel> models = Translator.translateFromListResponse(response, indexId);
    assertThat(models).isEqualTo(Arrays.asList(expectedModel));
  }
}
