package ir.piana.boot.endpoint.manager;

import ir.piana.boot.endpoint.core.manager.info.EndpointClientInfo;

import java.util.List;

public interface EndpointSolutionManager {
    List<EndpointClientInfo> getEndpointClients(
            String serviceName, long merchantId);
}
