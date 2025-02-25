package ir.piana.boot.endpoint.operation;

import ir.piana.boot.utils.restclient.request.RestClientExecutor;

public interface EndpointOperation<T, R> {
    R doRequest(RestClientExecutor restClientExecutor, T requestDto) throws EndpointOperationException;
//    R doRequest(RestClient restClient, T requestDto) throws EndpointOperationException;
    String endpointName();
    String serviceName();
}
