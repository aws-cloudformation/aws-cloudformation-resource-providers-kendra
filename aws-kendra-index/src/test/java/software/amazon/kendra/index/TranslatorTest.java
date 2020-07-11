package software.amazon.kendra.index;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TranslatorTest {

    @Test
    void translateToPostCreateUpdateRequestNullDocumentMetadataConfiguration() {
        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(ResourceModel.builder().build());
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isEmpty();
    }

    @Test
    void translateToPostCreateUpdateRequestDocumentMetadataConfigurationName() {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String name = "name";
        documentMetadataConfigurationBuilder.name(name);
        resourceModelBuilder.documentMetadataConfigurationUpdates(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModelBuilder.build());
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isNotEmpty();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).name())
                .isEqualTo(name);
    }

    @Test
    void translateToPostCreateUpdateRequestDocumentMetadataConfigurationType() {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String type = "type";
        documentMetadataConfigurationBuilder.type(type);
        resourceModelBuilder.documentMetadataConfigurationUpdates(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModelBuilder.build());
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isNotEmpty();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).typeAsString())
                .isEqualTo(type);
    }

    @Test
    void translateToPostCreateUpdateRequestDocumentMetadataConfigurationRelevance() {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        Relevance.RelevanceBuilder relevanceBuilder = Relevance.builder();
        String rankOrder = "rankOrder";
        relevanceBuilder.rankOrder(rankOrder);
        String duration = "duration";
        relevanceBuilder.duration(duration);
        Integer importance = 2;
        relevanceBuilder.importance(importance);
        Boolean freshness = true;
        relevanceBuilder.freshness(freshness);
        String key = "key";
        Integer value = 5;
        relevanceBuilder.valueImportanceMap(
                Arrays.asList(
                        ValueImportanceItem
                                .builder()
                                .key(key)
                                .value(value)
                                .build())
        );
        documentMetadataConfigurationBuilder.relevance(relevanceBuilder.build());
        resourceModelBuilder.documentMetadataConfigurationUpdates(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModelBuilder.build());
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isNotEmpty();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).relevance().importance())
                .isEqualTo(importance);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).relevance().duration())
                .isEqualTo(duration);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).relevance().rankOrderAsString())
                .isEqualTo(rankOrder);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).relevance().freshness())
                .isEqualTo(freshness);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).relevance().valueImportanceMap().get(key))
                .isEqualTo(value);
    }

    @Test
    void translateToPostCreateUpdateRequestDocumentMetadataConfigurationSearch() {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        Search.SearchBuilder searchBuilder = Search.builder();
        searchBuilder.displayable(true);
        searchBuilder.facetable(true);
        searchBuilder.searchable(true);

        documentMetadataConfigurationBuilder.search(searchBuilder.build());
        resourceModelBuilder.documentMetadataConfigurationUpdates(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModelBuilder.build());
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isNotEmpty();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).search().displayable())
                .isTrue();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).search().facetable())
                .isTrue();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).search().searchable())
                .isTrue();
    }
}
