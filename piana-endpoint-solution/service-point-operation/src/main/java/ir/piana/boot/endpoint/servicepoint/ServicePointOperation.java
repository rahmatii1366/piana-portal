package ir.piana.boot.endpoint.servicepoint;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.core.manager.info.EndpointClientInfo;
import ir.piana.boot.endpoint.operation.EndpointOperation;

import java.util.List;
import java.util.function.Function;

public abstract class ServicePointOperation<T, R> implements Function<ServicePointRequest<T>, R> {
    private final List<EndpointOperation<T, R>> endpointOperations;
    private final EndpointSolutionManager endpointSolutionManager;

    protected ServicePointOperation(
            List<EndpointOperation<T, R>> endpointOperations,
            EndpointSolutionManager endpointSolutionManager) {
        this.endpointOperations = endpointOperations;
        this.endpointSolutionManager = endpointSolutionManager;
    }

    @Override
    public final R apply(ServicePointRequest<T> servicePointRequest) {
        //ToDo should be complete
//        List<EndpointClientInfo> endpointClients = endpointSolutionManager.getEndpointClients(
//                serviceName(), servicePointRequest.getMerchantId());
        return null;
    }

    public abstract String serviceName();
}
