package software.amazon.kendra.datasource.convert;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.internal.BooleanDocument;
import software.amazon.awssdk.core.document.internal.ListDocument;
import software.amazon.awssdk.core.document.internal.MapDocument;
import software.amazon.awssdk.core.document.internal.NullDocument;
import software.amazon.awssdk.core.document.internal.NumberDocument;
import software.amazon.awssdk.core.document.internal.StringDocument;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;
import software.amazon.kendra.datasource.utils.DocumentTypeAdapter;

public class TemplateConverter {
    private static final Gson builder = new GsonBuilder()
            .registerTypeAdapter(Document.class, new DocumentTypeAdapter())
            .registerTypeAdapter(BooleanDocument.class, new DocumentTypeAdapter())
            .registerTypeAdapter(ListDocument.class, new DocumentTypeAdapter())
            .registerTypeAdapter(MapDocument.class, new DocumentTypeAdapter())
            .registerTypeAdapter(NullDocument.class, new DocumentTypeAdapter())
            .registerTypeAdapter(NumberDocument.class, new DocumentTypeAdapter())
            .registerTypeAdapter(StringDocument.class, new DocumentTypeAdapter())
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static software.amazon.awssdk.services.kendra.model.TemplateConfiguration toSdkDataSourceConfiguration(TemplateConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
                .template(builder.fromJson(builder.toJson(model.getTemplate()), Document.class))
                .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.TemplateConfiguration templateConfiguration) {
        return DataSourceConfiguration.builder()
                .templateConfiguration(toModel(templateConfiguration))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static TemplateConfiguration toModel(software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return TemplateConfiguration.builder()
                .template(builder.fromJson(builder.toJson(sdk.template().asMap()), Map.class))
                .build();
    }
}
