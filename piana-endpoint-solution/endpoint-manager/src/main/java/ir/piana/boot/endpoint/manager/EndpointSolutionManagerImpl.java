package ir.piana.boot.endpoint.manager;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.core.manager.OperationRequest;
import ir.piana.boot.endpoint.core.manager.info.*;
import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import ir.piana.boot.utils.errorprocessor.BadRequestTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

import java.util.*;

@RequiredArgsConstructor
final class EndpointSolutionManagerImpl implements EndpointSolutionManager {
    private final Map<Long, EndpointInfo> endpointMap;
    private final Map<Long, ServiceInfo> serviceMapById;
    private final Map<String, ServiceInfo> serviceMapByName;
    private final Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap;
    private final Map<Long, EndpointNetworkInfo> endpointNetworkMap;
    private final Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap;
    private final Map<ServiceInfo, List<EndpointApiInfo>> serviceToEndpointApiMap;
    private final Map<Long, EndpointClientInfo> endpointClientIdMapToEndpointClient;
    private final Map<EndpointInfo, List<EndpointClientInfo>> endpointToEndpointClientMap;
    private final Map<EndpointClientInfo, RestClient> endpointClientToRestClientMap;
    private final Map<Long, MerchantInfo> merchantIdToMerchantMap;
    private final Map<Long, Map<Long, List<EndpointInfo>>> serviceIdMapToMerchantIdMapToEndpoints;
    private final Map<Long, Map<Long, List<EndpointClientInfo>>> endpointIdMapToMerchantIdMapToEndpointClients;

    /*public List<EndpointClientInfo> getEndpointClients(
            String serviceName, long merchantId) {
        return null;
    }*/

    public <R, T> R sendRequest(String serviceName, OperationRequest<T> request) {
        long serviceId = Optional.ofNullable(serviceMapByName.get(serviceName))
                .orElseThrow(BadRequestTypes.API_NOT_EXIST::newException).id();
        List<EndpointInfo> endpointInfos = Optional.ofNullable(serviceIdMapToMerchantIdMapToEndpoints
                .computeIfAbsent(serviceId, k -> new HashMap<>()).get(request.getMerchantId()))
                .orElseThrow(BadRequestTypes.API_NOT_EXIST::newException);
        return null;
    }
}
