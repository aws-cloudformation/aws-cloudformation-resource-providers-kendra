package software.amazon.kendra.datasource.convert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.core.document.Document;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;
import software.amazon.kendra.datasource.utils.DocumentTypeAdapter;

public class TemplateConverter {
    private static final Gson builder = new GsonBuilder()
            .registerTypeAdapter(Document.class, new DocumentTypeAdapter())
            .setPrettyPrinting()
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
                .template(builder.fromJson(builder.toJson(sdk.template()), Map.class))
                .build();
    }
}
