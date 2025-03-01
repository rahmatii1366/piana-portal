package ir.piana.boot.endpoint.core.manager;

public interface EndpointSolutionManager {
    //ToDo should be complete : return Endpoint manager
    <R, T> R sendRequest(String serviceName, OperationRequest<T> request);
}
