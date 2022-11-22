package software.amazon.kendra.datasource.convert;

import software.amazon.awssdk.core.document.Document;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;

public class TemplateConverter {
    public static software.amazon.awssdk.services.kendra.model.TemplateConfiguration toSdkDataSourceConfiguration(TemplateConfiguration model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
                .template(Document.fromString(model.getTemplate()))
                .build();
    }

    public static DataSourceConfiguration toModelDataSourceConfiguration(
            software.amazon.awssdk.services.kendra.model.TemplateConfiguration templateConfiguration) {
        return DataSourceConfiguration.builder()
                .templateConfiguration(toModel(templateConfiguration))
                .build();
    }

    private static TemplateConfiguration toModel(software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdk) {
        if (sdk == null) {
            return null;
        }
        return TemplateConfiguration.builder()
                .template(sdk.template().asString())
                .build();
    }
}
