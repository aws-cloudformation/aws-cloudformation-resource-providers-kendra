package software.amazon.kendra.featuredresultsset;

import java.net.URI;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.endpoints.internal.DefaultKendraEndpointProvider;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  public static KendraClient getClient() {
    return KendraClient.builder()
        .httpClient(LambdaWrapper.HTTP_CLIENT)
        .build();
  }
}
