package software.amazon.kendra.datasource.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.protocols.json.internal.unmarshall.document.DocumentUnmarshaller;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentTypeAdapterTest {

    private static final String TYPE = "type";

    private static final String SERIALIZED_DOCUMENT = "{\"type\":\"test\"}";
    private static final Document DOCUMENT = Document.mapBuilder().putString(TYPE, "test").build();
    private Gson gson;

    @BeforeEach
    void setup() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Document.class, new DocumentTypeAdapter())
                .create();
    }

    @Test
    public void documentSerialization_valid_argument_success() {
        assertEquals(SERIALIZED_DOCUMENT, gson.toJson(DOCUMENT, Document.class));
    }

    @Test
    public void documentDeserialization_valid_argument_success() {
        assertEquals(DOCUMENT, gson.fromJson(SERIALIZED_DOCUMENT, Document.class));
    }
}
