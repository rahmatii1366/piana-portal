package ir.piana.boot.endpoint.manager.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EndpointSolutionManager {
    final Map<Long, EndpointInfo> endpointMap;
    final Map<Long, ServiceInfo> serviceMap;
    final Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap;
    final Map<Long, EndpointNetworkInfo> endpointNetworkMap;
    final Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap;
    final Map<ServiceInfo, List<EndpointApiInfo>> serviceToEndpointApiMap;
    final Map<EndpointInfo, List<EndpointClientInfo>> endpointToEndpointClientMap;
    final Map<EndpointClientInfo, RestClient> endpointClientToRestClientMap;
}
