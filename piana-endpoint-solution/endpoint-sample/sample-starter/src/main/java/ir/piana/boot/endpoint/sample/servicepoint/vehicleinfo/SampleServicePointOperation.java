package ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.operation.EndpointOperation;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBRequest;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBResponse;
import ir.piana.boot.endpoint.servicepoint.ServicePointOperation;

import java.util.List;

public class SampleServicePointOperation
        extends ServicePointOperation<SampleBRequest, SampleBResponse> {
//    private final List<EndpointOperation<SampleBRequest, SampleBResponse>> endpointOperations;

    public SampleServicePointOperation(
            List<EndpointOperation<SampleBRequest, SampleBResponse>> endpointOperations,
            EndpointSolutionManager endpointSolutionManager) {
        super(endpointOperations, endpointSolutionManager);
    }

    @Override
    public String serviceName() {
        return "sample";
    }
}
