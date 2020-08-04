package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.kendra.datasource.ServiceNowConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceNowConverterTest {

    @Test
    void testToSdkStringsTopLevel() {
        String hostUrl = "hostUrl";
        String secretArn = "secretArn";
        String version = "version";
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .hostUrl(hostUrl)
                .secretArn(secretArn)
                .serviceNowBuildVersion(version)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .hostUrl(hostUrl)
                        .secretArn(secretArn)
                        .serviceNowBuildVersion(version)
                        .build();

        assertThat(ServiceNowConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToModelStringsTopLevel() {
        String hostUrl = "hostUrl";
        String secretArn = "secretArn";
        String version = "version";
        ServiceNowConfiguration model = ServiceNowConfiguration
                .builder()
                .hostUrl(hostUrl)
                .secretArn(secretArn)
                .serviceNowBuildVersion(version)
                .build();

        software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.ServiceNowConfiguration
                        .builder()
                        .hostUrl(hostUrl)
                        .secretArn(secretArn)
                        .serviceNowBuildVersion(version)
                        .build();

        assertThat(ServiceNowConverter.toModel(sdk)).isEqualTo(model);
    }
}
