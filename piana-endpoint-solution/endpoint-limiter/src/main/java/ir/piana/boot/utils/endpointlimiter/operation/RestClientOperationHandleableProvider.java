package ir.piana.boot.utils.endpointlimiter.operation;

import org.springframework.web.client.RestClient;

public interface RestClientOperationHandleableProvider {
    RestClientOperationHandleable provide(String servicePointName, String endpointName);
}
