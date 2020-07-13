package software.amazon.kendra.index;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TranslatorTest {

    @Test
    void translateToSdkDocumentMetadataConfigurationListNullDocumentMetadataConfiguration() {
        assertThat(Translator.translateToSdkDocumentMetadataConfigurationList(null))
                .isEmpty();
    }

    @Test
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationName() {
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String name = "name";
        documentMetadataConfigurationBuilder.name(name);

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration sdkDocumentMetadataConfiguration =
                Translator.translateToSdkDocumentMetadataConfigurationList(Arrays.asList(documentMetadataConfigurationBuilder.build())).get(0);
        assertThat(sdkDocumentMetadataConfiguration.name()).isEqualTo(name);
        assertThat(sdkDocumentMetadataConfiguration.type()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.relevance()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.search()).isNull();
    }

    @Test
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationType() {
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String type = "type";
        documentMetadataConfigurationBuilder.type(type);

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration sdkDocumentMetadataConfiguration =
                Translator.translateToSdkDocumentMetadataConfigurationList(Arrays.asList(documentMetadataConfigurationBuilder.build())).get(0);
        assertThat(sdkDocumentMetadataConfiguration.typeAsString())
                .isEqualTo(type);
        assertThat(sdkDocumentMetadataConfiguration.name()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.relevance()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.search()).isNull();
    }

    @Test
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationRelevance() {
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
        relevanceBuilder.valueImportanceItems(
                Arrays.asList(
                        ValueImportanceItem
                                .builder()
                                .key(key)
                                .value(value)
                                .build())
        );
        documentMetadataConfigurationBuilder.relevance(relevanceBuilder.build());
        resourceModelBuilder.documentMetadataConfigurations(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration sdkDocumentMetadataConfiguration =
                Translator.translateToSdkDocumentMetadataConfigurationList(Arrays.asList(documentMetadataConfigurationBuilder.build())).get(0);
        assertThat(sdkDocumentMetadataConfiguration.relevance().importance())
                .isEqualTo(importance);
        assertThat(sdkDocumentMetadataConfiguration.relevance().duration())
                .isEqualTo(duration);
        assertThat(sdkDocumentMetadataConfiguration.relevance().rankOrderAsString())
                .isEqualTo(rankOrder);
        assertThat(sdkDocumentMetadataConfiguration.relevance().freshness())
                .isEqualTo(freshness);
        assertThat(sdkDocumentMetadataConfiguration.relevance().valueImportanceMap().get(key))
                .isEqualTo(value);

        assertThat(sdkDocumentMetadataConfiguration.name()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.type()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.search()).isNull();
    }

    @Test
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationSearch() {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        Search.SearchBuilder searchBuilder = Search.builder();
        searchBuilder.displayable(true);
        searchBuilder.facetable(true);
        searchBuilder.searchable(true);

        documentMetadataConfigurationBuilder.search(searchBuilder.build());
        resourceModelBuilder.documentMetadataConfigurations(
                Arrays.asList(documentMetadataConfigurationBuilder.build()));

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration sdkDocumentMetadataConfiguration =
                Translator.translateToSdkDocumentMetadataConfigurationList(Arrays.asList(documentMetadataConfigurationBuilder.build())).get(0);
        assertThat(sdkDocumentMetadataConfiguration.search().displayable())
                .isTrue();
        assertThat(sdkDocumentMetadataConfiguration.search().facetable())
                .isTrue();
        assertThat(sdkDocumentMetadataConfiguration.search().searchable())
                .isTrue();
        assertThat(sdkDocumentMetadataConfiguration.name()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.type()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.relevance()).isNull();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListNull() {
        assertThat(Translator.translateFromSdkDocumentMetadataConfigurationList(null)).isEmpty();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListName() {
        String name = "name";
        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.builder()
                .name(name);

        DocumentMetadataConfiguration modelDocumentMetadataConfiguration = Translator.translateFromSdkDocumentMetadataConfigurationList(
                Arrays.asList(sdkDocumentMetadataConfigurationBuilder.build())).get(0);
        assertThat(modelDocumentMetadataConfiguration.getName()).isEqualTo(name);

        assertThat(modelDocumentMetadataConfiguration.getType()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getRelevance()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getSearch()).isNull();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListType() {
        String type = "type";
        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.builder()
                        .type(type);

        DocumentMetadataConfiguration modelDocumentMetadataConfiguration = Translator.translateFromSdkDocumentMetadataConfigurationList(
                Arrays.asList(sdkDocumentMetadataConfigurationBuilder.build())).get(0);
        assertThat(modelDocumentMetadataConfiguration.getType()).isEqualTo(type);

        assertThat(modelDocumentMetadataConfiguration.getName()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getRelevance()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getSearch()).isNull();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListRelevance() {
        software.amazon.awssdk.services.kendra.model.Relevance.Builder sdkRelevanceBuilder =
                software.amazon.awssdk.services.kendra.model.Relevance.builder();
        String rankOrder = "rankOrder";
        sdkRelevanceBuilder.rankOrder(rankOrder);
        String duration = "duration";
        sdkRelevanceBuilder.duration(duration);
        Integer importance = 2;
        sdkRelevanceBuilder.importance(importance);
        Boolean freshness = true;
        sdkRelevanceBuilder.freshness(freshness);
        String key = "key";
        Integer value = 5;
        HashMap<String, Integer> importanceMap = new HashMap<>();
        importanceMap.put(key, value);
        sdkRelevanceBuilder.valueImportanceMap(importanceMap);

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.builder()
                        .relevance(sdkRelevanceBuilder.build());


        DocumentMetadataConfiguration modelDocumentMetadataConfiguration = Translator.translateFromSdkDocumentMetadataConfigurationList(
                Arrays.asList(sdkDocumentMetadataConfigurationBuilder.build())).get(0);
        Relevance modelRelevance = modelDocumentMetadataConfiguration.getRelevance();
        assertThat(modelRelevance.getDuration()).isEqualTo(duration);
        assertThat(modelRelevance.getRankOrder()).isEqualTo(rankOrder);
        assertThat(modelRelevance.getImportance()).isEqualTo(importance);
        assertThat(modelRelevance.getFreshness()).isEqualTo(freshness);

        assertThat(modelDocumentMetadataConfiguration.getName()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getType()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getSearch()).isNull();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListSearch() {
        software.amazon.awssdk.services.kendra.model.Search.Builder sdkSearchBuilder =
                software.amazon.awssdk.services.kendra.model.Search.builder();
        sdkSearchBuilder.searchable(true);
        sdkSearchBuilder.facetable(true);
        sdkSearchBuilder.displayable(true);

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.builder()
                        .search(sdkSearchBuilder.build());


        DocumentMetadataConfiguration modelDocumentMetadataConfiguration = Translator.translateFromSdkDocumentMetadataConfigurationList(
                Arrays.asList(sdkDocumentMetadataConfigurationBuilder.build())).get(0);
        Search modelSearch = modelDocumentMetadataConfiguration.getSearch();
        assertThat(modelSearch.getSearchable()).isTrue();
        assertThat(modelSearch.getFacetable()).isTrue();
        assertThat(modelSearch.getDisplayable()).isTrue();

        assertThat(modelDocumentMetadataConfiguration.getName()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getType()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getRelevance()).isNull();
    }

}
