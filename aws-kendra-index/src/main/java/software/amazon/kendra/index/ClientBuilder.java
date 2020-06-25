package software.amazon.kendra.index;

import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  public static KendraClient getClient() {
    return KendraClient.builder().httpClient(LambdaWrapper.HTTP_CLIENT).build();
  }
}
