package ir.piana.boot.endpoint.operation;

import org.springframework.web.client.RestClient;

public interface EndpointOperation<T, R> {
    R doRequest(RestClient restClient, T requestDto) throws EndpointOperationException;
    String endpointName();
    String serviceName();
}
