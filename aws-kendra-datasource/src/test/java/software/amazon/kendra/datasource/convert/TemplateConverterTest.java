package software.amazon.kendra.datasource.convert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.document.Document;
import software.amazon.kendra.datasource.DataSourceConfiguration;
import software.amazon.kendra.datasource.TemplateConfiguration;
import software.amazon.kendra.datasource.utils.DocumentTypeAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;


public class TemplateConverterTest {
    private static final String DATASOURCE_CONFIGURATION =
            "testdata/datasourceconfiguration/valid_datasource_configuration.json";

    @Test
    public void testSdkDataSourceConfiguration_Null() {
        assertThat(TemplateConverter.toSdkDataSourceConfiguration(null))
                .isEqualTo(null);
    }

    @Test
    public void testModelDataSourceConfiguration_Null() {
        assertThat(TemplateConverter.toModelDataSourceConfiguration(null)
                .getTemplateConfiguration()).isEqualTo(null);
    }

    @Test
    public void testSDKDataSourceConfigurationWithValidConfig() throws IOException {
        DataSourceConfiguration dataSourceConfiguration = DataSourceConfiguration.builder()
                .templateConfiguration(TemplateConfiguration.builder()
                        .template(readFileFromLocal(DATASOURCE_CONFIGURATION))
                        .build())
                .build();

        software.amazon.awssdk.services.kendra.model.TemplateConfiguration expectedDataSourceConfiguration =
                software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
                        .template(getTemplate(DATASOURCE_CONFIGURATION)).build();

        assertThat(TemplateConverter.toSdkDataSourceConfiguration(dataSourceConfiguration.getTemplateConfiguration()))
                .isEqualTo(expectedDataSourceConfiguration);

    }

    @Test
    public void testModelDataSourceConfigurationWithValidConfig() throws IOException {
        software.amazon.awssdk.services.kendra.model.TemplateConfiguration sdkDataSourceConfiguration =
                software.amazon.awssdk.services.kendra.model.TemplateConfiguration.builder()
                        .template(getTemplate(DATASOURCE_CONFIGURATION)).build();

        DataSourceConfiguration expectedDataSourceConfiguration = DataSourceConfiguration.builder()
                .templateConfiguration(TemplateConfiguration.builder()
                        .template(readFileFromLocal(DATASOURCE_CONFIGURATION))
                        .build())
                .build();

        Gson gson = new Gson();
        DataSourceConfiguration result = TemplateConverter.toModelDataSourceConfiguration(sdkDataSourceConfiguration);
        JsonObject resultTemplateConfig = gson.fromJson(result.getTemplateConfiguration().getTemplate(), JsonObject.class);

        JsonObject expectedDataSourceConfigurationTemplateConfig = gson.fromJson(
            expectedDataSourceConfiguration.getTemplateConfiguration().getTemplate(), JsonObject.class
        );

        assertThat(expectedDataSourceConfigurationTemplateConfig)
                .isEqualTo(resultTemplateConfig);
    }

    private Document getTemplate(String filePath) throws IOException {
        Gson builder = new GsonBuilder()
                .registerTypeAdapter(Document.class, new DocumentTypeAdapter())
                .create();
        return builder.fromJson(this.readFileFromLocal(filePath), Document.class);
    }

    private String readFileFromLocal(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
    }
}
