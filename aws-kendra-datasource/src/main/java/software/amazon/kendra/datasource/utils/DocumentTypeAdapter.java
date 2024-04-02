package software.amazon.kendra.datasource.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.protocols.json.internal.unmarshall.document.DocumentUnmarshaller;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;

public class DocumentTypeAdapter implements JsonDeserializer<Document>, JsonSerializer<Document> {
    private final DocumentUnmarshaller documentUnmarshaller = new DocumentUnmarshaller();

    @Override
    public Document deserialize(
            final JsonElement jsonElement, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        final JsonNodeParser jsonNodeParser = JsonNodeParser.create();
        final JsonNode jsonNode = jsonNodeParser.parse(jsonElement.toString());
        return jsonNode.visit(documentUnmarshaller);
    }

    @Override
    public JsonElement serialize(
            final Document document, final Type type, final JsonSerializationContext context) {
        if(document.isNumber()) {
            if (document.asNumber().stringValue().contains(".")) {
                return context.serialize(document.asNumber().doubleValue());
            }
            return context.serialize(document.asNumber().intValue());
        }
        return context.serialize(document.unwrap());
    }
}
