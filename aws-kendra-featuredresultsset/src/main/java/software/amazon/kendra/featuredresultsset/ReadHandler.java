package software.amazon.kendra.featuredresultsset;

import java.util.List;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetRequest;
import software.amazon.awssdk.services.kendra.model.DescribeFeaturedResultsSetResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;
    private FeaturedResultsArnBuilder frsArnBuilder;

    public ReadHandler() {
      super();
      frsArnBuilder = new FeaturedResultsArn();
    }

    public ReadHandler(FeaturedResultsArnBuilder frsArnBuilder) {
      this.frsArnBuilder = frsArnBuilder;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        this.logger = logger;
        String indexId = request.getDesiredResourceState().getIndexId();
        String frsArn = frsArnBuilder.build(request);

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        // STEP 1 [initialize a proxy context]
        return proxy.initiate("AWS-Kendra-FeaturedResultsSet::Read", proxyClient, request.getDesiredResourceState(), callbackContext)
            .translateToServiceRequest(Translator::translateToReadRequest)
            .makeServiceCall((describeRequest, client) -> {
                DescribeFeaturedResultsSetResponse describeResponse;
                try {
                  describeResponse = client.injectCredentialsAndInvokeV2(describeRequest, client.client()::describeFeaturedResultsSet);
                } catch (ResourceNotFoundException e) {
                  throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeRequest.featuredResultsSetId(), e);
                } catch (final AwsServiceException e) {
                    throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
                }

                logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
                return describeResponse;
            })
            .done(describeResponse -> ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(describeResponse, frsArn, indexId)));
    }
}
