package software.amazon.kendra.datasource;

import static software.amazon.kendra.datasource.ApiName.LIST_DATA_SOURCES;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.AccessDeniedException;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesRequest;
import software.amazon.awssdk.services.kendra.model.ListDataSourcesResponse;
import software.amazon.awssdk.services.kendra.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kendra.model.ThrottlingException;
import software.amazon.awssdk.services.kendra.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<KendraClient> proxyClient,
        final Logger logger) {

        final String indexId = request.getDesiredResourceState().getIndexId();

        final ListDataSourcesRequest listDataSourcesRequest = Translator.translateToListRequest(
            indexId,request.getNextToken());

        ListDataSourcesResponse listDataSourcesResponse = listDataSources(listDataSourcesRequest, proxyClient);

        String nextToken = listDataSourcesResponse.nextToken();

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListResponse(listDataSourcesResponse, indexId))
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private ListDataSourcesResponse listDataSources(
        ListDataSourcesRequest request,
        ProxyClient<KendraClient> proxyClient) {
        try {
            return proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::listDataSources);
        } catch (ValidationException e) {
            throw new CfnInvalidRequestException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(LIST_DATA_SOURCES, e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(LIST_DATA_SOURCES, e);
        } catch (final AwsServiceException e) {
            throw new CfnGeneralServiceException(LIST_DATA_SOURCES, e);
        }
    }
}
