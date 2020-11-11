package software.amazon.kendra.index;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.DeleteIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.DocumentAttributeValueType;
import software.amazon.awssdk.services.kendra.model.IndexConfigurationSummary;
import software.amazon.awssdk.services.kendra.model.IndexEdition;
import software.amazon.awssdk.services.kendra.model.KeyLocation;
import software.amazon.awssdk.services.kendra.model.ListIndicesRequest;
import software.amazon.awssdk.services.kendra.model.ListIndicesResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.TagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UntagResourceRequest;
import software.amazon.awssdk.services.kendra.model.UpdateIndexRequest;
import software.amazon.awssdk.services.kendra.model.UserContextPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TranslatorTest {

    @Test
    void translateToSdkDocumentMetadataConfigurationListNullDocumentMetadataConfiguration() throws TranslatorValidationException {
        assertThat(Translator.translateToSdkDocumentMetadataConfigurationList(null))
                .isEmpty();
    }

    @Test
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationName() throws TranslatorValidationException {
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
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationType() throws TranslatorValidationException {
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
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationRelevance() throws TranslatorValidationException {
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
    void translateToSdkDocumentMetadataConfigurationListDocumentMetadataConfigurationSearch() throws TranslatorValidationException {
        ResourceModel.ResourceModelBuilder resourceModelBuilder = ResourceModel.builder();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        Search.SearchBuilder searchBuilder = Search.builder();
        searchBuilder.displayable(true);
        searchBuilder.facetable(true);
        searchBuilder.searchable(true);
        searchBuilder.sortable(true);

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
        assertThat(sdkDocumentMetadataConfiguration.search().sortable())
                .isTrue();
        assertThat(sdkDocumentMetadataConfiguration.name()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.type()).isNull();
        assertThat(sdkDocumentMetadataConfiguration.relevance()).isNull();
    }

    @Test
    void testTranslateFromSdkDocumentMetadataConfigurationListNull() {
        assertThat(Translator.translateFromSdkDocumentMetadataConfigurationList(null)).isNull();
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
        sdkSearchBuilder.sortable(true);

        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.Builder sdkDocumentMetadataConfigurationBuilder =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration.builder()
                        .search(sdkSearchBuilder.build());


        DocumentMetadataConfiguration modelDocumentMetadataConfiguration = Translator.translateFromSdkDocumentMetadataConfigurationList(
                Arrays.asList(sdkDocumentMetadataConfigurationBuilder.build())).get(0);
        Search modelSearch = modelDocumentMetadataConfiguration.getSearch();
        assertThat(modelSearch.getSearchable()).isTrue();
        assertThat(modelSearch.getSortable()).isTrue();
        assertThat(modelSearch.getFacetable()).isTrue();
        assertThat(modelSearch.getDisplayable()).isTrue();

        assertThat(modelDocumentMetadataConfiguration.getName()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getType()).isNull();
        assertThat(modelDocumentMetadataConfiguration.getRelevance()).isNull();
    }

    @Test
    void testTranslateToCreateRequestNameRoleDescriptionEdition() {
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String edition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.USER_TOKEN.toString();
        UserTokenConfiguration userTokenConfiguration = UserTokenConfiguration.builder()
            .jsonTokenTypeConfiguration(JsonTokenTypeConfiguration.builder()
                .userNameAttributeField("user")
                .groupAttributeField("group")
                .build())
            .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .edition(edition)
                .userContextPolicy(userContextPolicy)
                .userTokenConfigurations(Arrays.asList(userTokenConfiguration))
                .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.description()).isEqualTo(description);
        assertThat(actualCreateIndexRequest.name()).isEqualTo(name);
        assertThat(actualCreateIndexRequest.editionAsString()).isEqualTo(edition);
        assertThat(actualCreateIndexRequest.roleArn()).isEqualTo(roleArn);
        assertThat(actualCreateIndexRequest.userContextPolicy().toString()).isEqualTo(userContextPolicy);
        assertThat(actualCreateIndexRequest.userTokenConfigurations().get(0).jsonTokenTypeConfiguration().groupAttributeField()).isEqualTo("group");
        assertThat(actualCreateIndexRequest.userTokenConfigurations().get(0).jsonTokenTypeConfiguration().userNameAttributeField()).isEqualTo("user");
    }

    @Test
    void testTranslateToCreateRequestWithJwtTokenTypeConfiguration() {
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String edition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.USER_TOKEN.toString();
        UserTokenConfiguration userTokenConfiguration = UserTokenConfiguration.builder()
            .jwtTokenTypeConfiguration(JwtTokenTypeConfiguration.builder()
                .keyLocation(KeyLocation.URL.toString())
                .uRL("test_url")
                .build())
            .build();
        ResourceModel resourceModel = ResourceModel
            .builder()
            .name(name)
            .description(description)
            .roleArn(roleArn)
            .edition(edition)
            .userContextPolicy(userContextPolicy)
            .userTokenConfigurations(Arrays.asList(userTokenConfiguration))
            .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.description()).isEqualTo(description);
        assertThat(actualCreateIndexRequest.name()).isEqualTo(name);
        assertThat(actualCreateIndexRequest.editionAsString()).isEqualTo(edition);
        assertThat(actualCreateIndexRequest.roleArn()).isEqualTo(roleArn);
        assertThat(actualCreateIndexRequest.userContextPolicy().toString()).isEqualTo(userContextPolicy);
        assertThat(actualCreateIndexRequest.userTokenConfigurations().get(0).jwtTokenTypeConfiguration().keyLocation().toString()).isEqualTo(KeyLocation.URL.toString());
        assertThat(actualCreateIndexRequest.userTokenConfigurations().get(0).jwtTokenTypeConfiguration().url()).isEqualTo("test_url");
    }

    @Test
    void testTranslateToCreateRequestWithUserContextPolicyAsAttributeFilter() {
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String edition = IndexEdition.ENTERPRISE_EDITION.toString();
        String userContextPolicy = UserContextPolicy.ATTRIBUTE_FILTER.toString();
        ResourceModel resourceModel = ResourceModel
            .builder()
            .name(name)
            .description(description)
            .roleArn(roleArn)
            .edition(edition)
            .userContextPolicy(userContextPolicy)
            .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.description()).isEqualTo(description);
        assertThat(actualCreateIndexRequest.name()).isEqualTo(name);
        assertThat(actualCreateIndexRequest.editionAsString()).isEqualTo(edition);
        assertThat(actualCreateIndexRequest.roleArn()).isEqualTo(roleArn);
        assertThat(actualCreateIndexRequest.userContextPolicy().toString()).isEqualTo(userContextPolicy);
        assertThat(actualCreateIndexRequest.userTokenConfigurations()).isNullOrEmpty();
    }

    @Test
    void testTranslateToCreateRequestTags() {
        String key = "key";
        String value = "value";
        Tag tag = Tag.builder().key(key).value(value).build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .tags(Arrays.asList(tag))
                .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.tags().size()).isEqualTo(1);
        assertThat(actualCreateIndexRequest.tags().get(0).key()).isEqualTo(key);
        assertThat(actualCreateIndexRequest.tags().get(0).value()).isEqualTo(value);
    }

    @Test
    void testTranslateToCreateRequestServerSideEncryptionConfiguration() {
        String kmsKeyId = "kmsKeyId";
        ServerSideEncryptionConfiguration serverSideEncryptionConfiguration = ServerSideEncryptionConfiguration
                .builder()
                .kmsKeyId(kmsKeyId)
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.serverSideEncryptionConfiguration().kmsKeyId()).isEqualTo(kmsKeyId);
    }

    @Test
    void testTranslateToCreateRequestServerSideEncryptionConfigurationNullKmsKeyId() {
        ServerSideEncryptionConfiguration serverSideEncryptionConfiguration = ServerSideEncryptionConfiguration
                .builder()
                .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .build();
        CreateIndexRequest actualCreateIndexRequest = Translator.translateToCreateRequest(resourceModel);
        assertThat(actualCreateIndexRequest.serverSideEncryptionConfiguration()).isNull();
    }

    @Test
    void testTranslateToReadRequest() {
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .build();
        DescribeIndexRequest describeIndexRequest = Translator.translateToReadRequest(resourceModel);
        assertThat(describeIndexRequest.id()).isEqualTo(id);
    }

    @Test
    void testTranslateToDeleteRequest() {
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .build();
        DeleteIndexRequest deleteIndexRequest = Translator.translateToDeleteRequest(resourceModel);
        assertThat(deleteIndexRequest.id()).isEqualTo(id);
    }

    @Test
    void testTranslateToPostCreateUpdateRequestDeveloperEdition() throws TranslatorValidationException {
        String id = "id";
        String name = "name";
        String type = DocumentAttributeValueType.STRING_VALUE.toString();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder.name(name).type(type);
        // roleArn should not be updated
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .roleArn("roleArn")
                .name("name")
                .description("description")
                .edition(IndexEdition.DEVELOPER_EDITION.toString())
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .build();

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModel);
        assertThat(updateIndexRequest.roleArn()).isNull();
        assertThat(updateIndexRequest.description()).isNull();
        assertThat(updateIndexRequest.name()).isNull();
        assertThat(updateIndexRequest.id()).isEqualTo(id);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().size()).isEqualTo(1);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).name()).isEqualTo(name);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).type().toString()).isEqualTo(type);
        assertThat(updateIndexRequest.capacityUnits()).isNull();
    }

    @Test
    void testTranslateToPostCreateUpdateRequest() throws TranslatorValidationException {
        String id = "id";
        String name = "name";
        String type = DocumentAttributeValueType.STRING_VALUE.toString();
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder.name(name).type(type);
        // roleArn should not be updated
        Integer queryCapacityUnits = 1;
        Integer storageCapacityUnits = 2;
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .roleArn("roleArn")
                .name("name")
                .description("description")
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .capacityUnits(CapacityUnitsConfiguration.builder()
                        .queryCapacityUnits(queryCapacityUnits)
                        .storageCapacityUnits(storageCapacityUnits)
                        .build())
                .build();

        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModel);
        assertThat(updateIndexRequest.roleArn()).isNull();
        assertThat(updateIndexRequest.description()).isNull();
        assertThat(updateIndexRequest.name()).isNull();
        assertThat(updateIndexRequest.id()).isEqualTo(id);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().size()).isEqualTo(1);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).name()).isEqualTo(name);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().get(0).type().toString()).isEqualTo(type);
        assertThat(updateIndexRequest.capacityUnits().queryCapacityUnits()).isEqualTo(queryCapacityUnits);
        assertThat(updateIndexRequest.capacityUnits().storageCapacityUnits()).isEqualTo(storageCapacityUnits);
    }

    @Test
    void testTranslateToUpdateRequest() throws TranslatorValidationException {
        String metadataName = "metadataName";
        String metadataType = "STRING_VALUE";
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder.name(metadataName);
        documentMetadataConfigurationBuilder.type(metadataType);
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String id = "id";
        Integer queryCapacityUnits = 1;
        Integer storageCapacityUnits = 2;
        String userContextPolicy = UserContextPolicy.USER_TOKEN.toString();
        UserTokenConfiguration userTokenConfiguration = UserTokenConfiguration.builder()
            .jsonTokenTypeConfiguration(JsonTokenTypeConfiguration.builder()
                .groupAttributeField("group")
                .userNameAttributeField("user")
                .build())
            .build();
        ResourceModel resourceModel = ResourceModel
                .builder()
                .name(name)
                .id(id)
                .description(description)
                .roleArn(roleArn)
                .userContextPolicy(userContextPolicy)
                .userTokenConfigurations(Arrays.asList(userTokenConfiguration))
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .capacityUnits(CapacityUnitsConfiguration
                        .builder()
                        .queryCapacityUnits(queryCapacityUnits)
                        .storageCapacityUnits(storageCapacityUnits)
                        .build())
                .build();
        ResourceModel prevModel = ResourceModel
                .builder()
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .userContextPolicy(UserContextPolicy.ATTRIBUTE_FILTER.toString())
                .build();
        UpdateIndexRequest updateIndexRequest = Translator.translateToUpdateRequest(resourceModel, prevModel);
        assertThat(updateIndexRequest.id()).isEqualTo(id);
        assertThat(updateIndexRequest.description()).isEqualTo(description);
        assertThat(updateIndexRequest.name()).isEqualTo(name);
        assertThat(updateIndexRequest.roleArn()).isEqualTo(roleArn);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().size()).isEqualTo(1);
        assertThat(updateIndexRequest.capacityUnits().queryCapacityUnits()).isEqualTo(queryCapacityUnits);
        assertThat(updateIndexRequest.capacityUnits().storageCapacityUnits()).isEqualTo(storageCapacityUnits);
        assertThat(updateIndexRequest.userContextPolicy().toString()).isEqualTo(userContextPolicy);
        assertThat(updateIndexRequest.userTokenConfigurations().size()).isEqualTo(1);
    }

    @Test
    void testTranslateToUpdateRequestDeveloperEdition() throws TranslatorValidationException {
        String metadataName = "metadataName";
        String metadataType = "STRING_VALUE";
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder.name(metadataName);
        documentMetadataConfigurationBuilder.type(metadataType);
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .name(name)
                .id(id)
                .description(description)
                .roleArn(roleArn)
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .edition(IndexEdition.DEVELOPER_EDITION.toString())
                .build();
        ResourceModel prevModel = ResourceModel
                .builder()
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfigurationBuilder.build()))
                .build();
        UpdateIndexRequest updateIndexRequest = Translator.translateToUpdateRequest(resourceModel, prevModel);
        assertThat(updateIndexRequest.id()).isEqualTo(id);
        assertThat(updateIndexRequest.description()).isEqualTo(description);
        assertThat(updateIndexRequest.name()).isEqualTo(name);
        assertThat(updateIndexRequest.roleArn()).isEqualTo(roleArn);
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates().size()).isEqualTo(1);
        assertThat(updateIndexRequest.capacityUnits()).isNull();
    }

    @Test
    void testTranslateToUpdateRequestUnsetInCloudFormation() throws TranslatorValidationException {
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .id(id)
                .build();
        UpdateIndexRequest updateIndexRequest = Translator.translateToUpdateRequest(resourceModel, ResourceModel.builder().build());
        assertThat(updateIndexRequest.description()).isEqualTo("");
        assertThat(updateIndexRequest.name()).isEqualTo("");
        assertThat(updateIndexRequest.roleArn()).isEqualTo("");
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isEmpty();
        assertThat(updateIndexRequest.capacityUnits().queryCapacityUnits()).isEqualTo(0);
        assertThat(updateIndexRequest.capacityUnits().storageCapacityUnits()).isEqualTo(0);
        assertThat(updateIndexRequest.userContextPolicy()).isNull();
        assertThat(updateIndexRequest.userTokenConfigurations()).isNullOrEmpty();
    }

    @Test
    void testTranslateToPostCreateUpdateRequestUnsetInCloudFormation() throws TranslatorValidationException {
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .id(id)
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .build();
        UpdateIndexRequest updateIndexRequest = Translator.translateToPostCreateUpdateRequest(resourceModel);
        assertThat(updateIndexRequest.description()).isNull();
        assertThat(updateIndexRequest.name()).isNull();
        assertThat(updateIndexRequest.roleArn()).isNull();
        assertThat(updateIndexRequest.documentMetadataConfigurationUpdates()).isEmpty();
        assertThat(updateIndexRequest.capacityUnits().queryCapacityUnits()).isEqualTo(0);
        assertThat(updateIndexRequest.capacityUnits().storageCapacityUnits()).isEqualTo(0);
    }

    @Test
    void testTranslateToListTagsRequest() {
        String arn = "arn";
        ListTagsForResourceRequest actual = Translator.translateToListTagsRequest(arn);
        ListTagsForResourceRequest expected = ListTagsForResourceRequest
                .builder()
                .resourceARN(arn)
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testTranslateToUntagResourceRequest() {
        String arn = "arn";
        String key = "key";
        String value = "value";
        Tag tag = Tag.builder().key(key).value(value).build();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(tag);
        UntagResourceRequest actual = Translator.translateToUntagResourceRequest(tags, arn);
        UntagResourceRequest expected = UntagResourceRequest
                .builder()
                .resourceARN(arn)
                .tagKeys(Arrays.asList(key))
                .build();
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    void testTranslateToTagResourceRequest() {
        String arn = "arn";
        String key = "key";
        String value = "value";
        Tag tag = Tag.builder().key(key).value(value).build();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(tag);
        TagResourceRequest actual = Translator.translateToTagResourceRequest(tags, arn);
        TagResourceRequest expected = TagResourceRequest
                .builder()
                .resourceARN(arn)
                .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                        .builder()
                        .key(key)
                        .value(value)
                        .build()))
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testTranslateFromReadResponse() {
        String id = "id";
        String name = "name";
        String description = "description";
        String roleArn = "roleArn";
        String edition = IndexEdition.ENTERPRISE_EDITION.toString();
        String kmsKeyId = "kmsKeyId";
        String userContextPolicy = software.amazon.awssdk.services.kendra.model.UserContextPolicy.USER_TOKEN.toString();
        software.amazon.awssdk.services.kendra.model.UserTokenConfiguration userTokenConfiguration = software.amazon.awssdk.services.kendra.model.UserTokenConfiguration.builder()
            .jwtTokenTypeConfiguration(software.amazon.awssdk.services.kendra.model.JwtTokenTypeConfiguration.builder()
                .keyLocation(KeyLocation.SECRET_MANAGER)
                .secretManagerArn("test_secrets_manager_arn")
                .build())
            .build();
        software.amazon.awssdk.services.kendra.model.ServerSideEncryptionConfiguration serverSideEncryptionConfiguration =
                software.amazon.awssdk.services.kendra.model.ServerSideEncryptionConfiguration
                        .builder()
                        .kmsKeyId(kmsKeyId)
                        .build();
        String metadataName = "metadataName";
        String metadataType = DocumentAttributeValueType.STRING_VALUE.toString();
        software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration documentMetadataConfiguration =
                software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration
                        .builder()
                        .name(metadataName)
                        .type(metadataType)
                        .build();
        Integer queryCapacityUnits = 1;
        Integer storageCapacityUnits = 2;
        DescribeIndexResponse describeIndexResponse = DescribeIndexResponse
                .builder()
                .id(id)
                .name(name)
                .roleArn(roleArn)
                .edition(edition)
                .description(description)
                .serverSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .documentMetadataConfigurations(Arrays.asList(documentMetadataConfiguration))
                .capacityUnits(software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration
                        .builder()
                        .queryCapacityUnits(queryCapacityUnits)
                        .storageCapacityUnits(storageCapacityUnits)
                        .build())
                .userContextPolicy(userContextPolicy)
                .userTokenConfigurations(Arrays.asList(userTokenConfiguration))
                .build();
        String tagKey = "tagKey";
        String tagValue = "tagValue";
        ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse
                .builder()
                .tags(Arrays.asList(software.amazon.awssdk.services.kendra.model.Tag
                        .builder()
                        .key(tagKey)
                        .value(tagValue)
                        .build()))
                .build();
        String arn = "arn";
        ResourceModel actual = Translator.translateFromReadResponse(describeIndexResponse, listTagsForResourceResponse, arn);
        assertThat(actual.getArn()).isEqualTo(arn);
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getDescription()).isEqualTo(description);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getRoleArn()).isEqualTo(roleArn);
        assertThat(actual.getEdition()).isEqualTo(edition);
        assertThat(actual.getDocumentMetadataConfigurations().size()).isEqualTo(1);
        assertThat(actual.getDocumentMetadataConfigurations().get(0).getName()).isEqualTo(metadataName);
        assertThat(actual.getDocumentMetadataConfigurations().get(0).getType()).isEqualTo(metadataType);
        assertThat(actual.getCapacityUnits().getQueryCapacityUnits()).isEqualTo(queryCapacityUnits);
        assertThat(actual.getCapacityUnits().getStorageCapacityUnits()).isEqualTo(storageCapacityUnits);
        assertThat(actual.getTags().size()).isEqualTo(1);
        assertThat(actual.getUserContextPolicy()).isEqualTo(userContextPolicy);
        assertThat(actual.getUserTokenConfigurations().get(0).getJwtTokenTypeConfiguration().getKeyLocation()).isEqualTo(KeyLocation.SECRET_MANAGER.toString());
        assertThat(actual.getUserTokenConfigurations().get(0).getJwtTokenTypeConfiguration().getSecretManagerArn()).isEqualTo("test_secrets_manager_arn");
    }

    @Test
    void testTranslateFromReadResponseNullEquivalentVCU() {
        DescribeIndexResponse describeIndexResponse = DescribeIndexResponse
                .builder()
                .edition(IndexEdition.DEVELOPER_EDITION)
                .capacityUnits(software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration
                        .builder()
                        .queryCapacityUnits(0)
                        .storageCapacityUnits(0)
                        .build())
                .userContextPolicy(UserContextPolicy.ATTRIBUTE_FILTER)
                .build();
        ResourceModel actual = Translator.translateFromReadResponse(
                describeIndexResponse,
                ListTagsForResourceResponse
                        .builder()
                        .build(),
                "arn");
        assertThat(actual.getCapacityUnits()).isNull();
    }

    @Test
    void testTranslateToListRequest() {
        String nextToken = "nextToken";
        ListIndicesRequest actual = Translator.translateToListRequest(nextToken);
        ListIndicesRequest expected = ListIndicesRequest.builder().nextToken(nextToken).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testTranslateFromListResponse() {
        String id1 = "id1";
        String id2 = "id2";
        ListIndicesResponse listIndicesResponse = ListIndicesResponse
                .builder()
                .indexConfigurationSummaryItems(Arrays.asList(IndexConfigurationSummary
                        .builder()
                        .id(id1)
                        .build(),
                        IndexConfigurationSummary
                                .builder()
                                .id(id2)
                                .build()))
                .build();
        List<ResourceModel> actual = Translator.translateFromListResponse(listIndicesResponse);
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0).getId()).isEqualTo(id1);
        assertThat(actual.get(1).getId()).isEqualTo(id2);
    }

    @Test
    void testTranslateToCapacityUnitsConfigurationUnsetEnterpriseEdition() {
        software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration
                capacityUnitsConfiguration = Translator
                .translateToCapacityUnitsConfiguration(null, IndexEdition.ENTERPRISE_EDITION.toString());
        assertThat(capacityUnitsConfiguration.queryCapacityUnits()).isEqualTo(0);
        assertThat(capacityUnitsConfiguration.storageCapacityUnits()).isEqualTo(0);
    }

    @Test
    void testTranslateToCapacityUnitsConfigurationUnsetDeveloperEdition() {
        software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration
                capacityUnitsConfiguration = Translator
                .translateToCapacityUnitsConfiguration(null, IndexEdition.DEVELOPER_EDITION.toString());
        assertThat(capacityUnitsConfiguration).isNull();
    }

    @Test
    void testTranslateToCapacityUnitsConfigurationSetDeveloperEdition() {
        software.amazon.awssdk.services.kendra.model.CapacityUnitsConfiguration
                capacityUnitsConfiguration = Translator
                .translateToCapacityUnitsConfiguration(CapacityUnitsConfiguration.builder().build(),
                        IndexEdition.DEVELOPER_EDITION.toString());
        assertThat(capacityUnitsConfiguration).isNull();
    }

    @Test
    void testTranslateToSdkDocumentMetadataConfigurationListThrowsValidationException() {
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String name = "name";
        String type = "type";
        documentMetadataConfigurationBuilder.name(name);
        documentMetadataConfigurationBuilder.type(type);

        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder2 =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder2.name("NotDefined");
        documentMetadataConfigurationBuilder2.type("type");


        assertThrows(TranslatorValidationException.class, () -> {
            Translator.translateToSdkDocumentMetadataConfigurationList(
                    Arrays.asList(documentMetadataConfigurationBuilder.build()),
                    Arrays.asList(documentMetadataConfigurationBuilder2.build()));
        });
    }

    @Test
    void testTranslateToSdkDocumentMetadataConfigurationListAddsDefaultReservedField() throws TranslatorValidationException {
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String name = "name";
        String type = "type";
        documentMetadataConfigurationBuilder.name(name);
        documentMetadataConfigurationBuilder.type(type);

        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder2 =
                DocumentMetadataConfiguration.builder();
        String customAttributeName = "_custom";
        String customAttributeType = "STRING_VALUE";
        documentMetadataConfigurationBuilder2.name(customAttributeName);
        documentMetadataConfigurationBuilder2.type(customAttributeType);


        List<software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration> sdkList =
                Translator.translateToSdkDocumentMetadataConfigurationList(
                        Arrays.asList(documentMetadataConfigurationBuilder.build(), documentMetadataConfigurationBuilder2.build()),
                        new ArrayList<>());

        assertThat(sdkList.size()).isEqualTo(2);
        assertThat(sdkList.get(1).name()).isEqualTo(customAttributeName);
        assertThat(sdkList.get(1).typeAsString()).isEqualTo(customAttributeType);
        assertThat(sdkList.get(1).search()).isNull();
        assertThat(sdkList.get(1).relevance()).isNull();
    }

    @Test
    void testTranslateToSdkDocumentMetadataConfigurationListAllDefined() throws TranslatorValidationException {
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder =
                DocumentMetadataConfiguration.builder();
        String name = "name";
        String type = "type";
        documentMetadataConfigurationBuilder.name(name);
        documentMetadataConfigurationBuilder.type(type);

        String customAttributeName = "_custom";
        String customAttributeType = "type";
        DocumentMetadataConfiguration.DocumentMetadataConfigurationBuilder documentMetadataConfigurationBuilder2 =
                DocumentMetadataConfiguration.builder();
        documentMetadataConfigurationBuilder2.name(customAttributeName);
        documentMetadataConfigurationBuilder2.type(customAttributeType);


        List<software.amazon.awssdk.services.kendra.model.DocumentMetadataConfiguration> sdkList =
                Translator.translateToSdkDocumentMetadataConfigurationList(
                        Arrays.asList(
                                documentMetadataConfigurationBuilder.build(),
                                documentMetadataConfigurationBuilder2.build()), new ArrayList<>());

        assertThat(sdkList.size()).isEqualTo(2);
    }

    @Test
    void testTranslateToPostCreateUpdateRequestDuplicateValueImportanceItems() {
        String id = "id";
        ResourceModel resourceModel = ResourceModel
                .builder()
                .edition(IndexEdition.ENTERPRISE_EDITION.toString())
                .documentMetadataConfigurations(Arrays.asList(
                        DocumentMetadataConfiguration
                                .builder()
                                .relevance(Relevance
                                        .builder()
                                        .valueImportanceItems(Arrays.asList(
                                                ValueImportanceItem
                                                        .builder()
                                                        .key("a")
                                                        .build(),
                                                ValueImportanceItem
                                                        .builder()
                                                        .key("a")
                                                        .build())
                                        )
                                        .build())
                                .build()
                ))
                .id(id)
                .build();

        assertThrows(TranslatorValidationException.class, () -> Translator.translateToPostCreateUpdateRequest(resourceModel));
    }
}
