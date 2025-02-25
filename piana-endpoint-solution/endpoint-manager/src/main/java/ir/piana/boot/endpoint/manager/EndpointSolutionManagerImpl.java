package ir.piana.boot.endpoint.manager;

import ir.piana.boot.endpoint.core.manager.info.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
final class EndpointSolutionManagerImpl implements EndpointSolutionManager {
    final Map<Long, EndpointInfo> endpointMap;
    final Map<Long, ServiceInfo> serviceMapById;
    final Map<String, ServiceInfo> serviceMapByName;
    final Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap;
    final Map<Long, EndpointNetworkInfo> endpointNetworkMap;
    final Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap;
    final Map<ServiceInfo, List<EndpointApiInfo>> serviceToEndpointApiMap;
    final Map<EndpointInfo, List<EndpointClientInfo>> endpointToEndpointClientMap;
    final Map<EndpointClientInfo, RestClient> endpointClientToRestClientMap;

    public List<EndpointClientInfo> getEndpointClients(
            String serviceName, long merchantId) {
        return null;
    }
}
