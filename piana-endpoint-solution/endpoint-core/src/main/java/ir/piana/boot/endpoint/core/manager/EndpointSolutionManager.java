package ir.piana.boot.endpoint.core.manager;

import ir.piana.boot.endpoint.core.manager.info.EndpointClientInfo;

import java.util.List;

public interface EndpointSolutionManager {
    //ToDo should be complete : return Endpoint manager
    List<EndpointClientInfo> getEndpointClients(
            String serviceName, long merchantId);
}
