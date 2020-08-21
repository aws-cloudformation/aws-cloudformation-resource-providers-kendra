package software.amazon.kendra.datasource;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.kendra.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.kendra.model.ResourceInUseException;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.kendra.datasource.ApiName.DESCRIBE_DATASOURCE;
import static software.amazon.kendra.datasource.ApiName.LIST_TAGS_FOR_RESOURCE;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;

    private DataSourceArnBuilder dataSourceArnBuilder;

    public ReadHandler() {
        super();
        this.dataSourceArnBuilder = new DataSourceArn();
    }

    public ReadHandler(DataSourceArnBuilder dataSourceArnBuilder) {
        this.dataSourceArnBuilder = dataSourceArnBuilder;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();
        final DescribeDataSourceRequest describeDataSourceRequest = Translator.translateToReadRequest(model);
        DescribeDataSourceResponse describeDataSourceResponse;
        try {
            describeDataSourceResponse = proxyClient.injectCredentialsAndInvokeV2(
                describeDataSourceRequest, proxyClient.client()::describeDataSource);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, describeDataSourceRequest.id(), e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(DESCRIBE_DATASOURCE, e);
        }
        // STEP 4 [Add List Tags for DataSource]
        String dataSourceArn = dataSourceArnBuilder.build(request);
        final ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsRequest(dataSourceArn);
        ListTagsForResourceResponse listTagsForResourceResponse;
        try {
            listTagsForResourceResponse = proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest,
                    proxyClient.client()::listTagsForResource);
        } catch (ResourceInUseException e) {
            throw new CfnGeneralServiceException(LIST_TAGS_FOR_RESOURCE, e);
        }
        return constructResourceModelFromResponse(describeDataSourceResponse, listTagsForResourceResponse, dataSourceArn);
    }

    /**
     * Implement client invocation of the read request through the proxyClient, which is already initialised with
     * caller credentials, correct region and retry settings
     * @param describeDataSourceResponse the aws service describe resource response
     * @return progressEvent indicating success, in progress with delay callback or failed state
     */
    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
        final DescribeDataSourceResponse describeDataSourceResponse,
        final ListTagsForResourceResponse listTagsForResourceResponse,
        final String dataSourceArn) {
        ResourceModel resourceModel =
            Translator.translateFromReadResponse(describeDataSourceResponse, listTagsForResourceResponse, dataSourceArn);
        return ProgressEvent.defaultSuccessHandler(resourceModel);
    }

}
