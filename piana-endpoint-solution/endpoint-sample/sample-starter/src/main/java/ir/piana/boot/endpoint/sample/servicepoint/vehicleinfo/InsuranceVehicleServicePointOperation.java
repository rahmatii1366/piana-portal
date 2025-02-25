package ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.operation.EndpointOperation;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoRequest;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoResponse;
import ir.piana.boot.endpoint.servicepoint.ServicePointOperation;

import java.util.List;

public class InsuranceVehicleServicePointOperation
        extends ServicePointOperation<VehicleInfoRequest, VehicleInfoResponse> {
//    private final List<EndpointOperation<VehicleInfoRequest, VehicleInfoResponse>> endpointOperations;

    public InsuranceVehicleServicePointOperation(
            List<EndpointOperation<VehicleInfoRequest, VehicleInfoResponse>> endpointOperations,
            EndpointSolutionManager endpointSolutionManager) {
        super(endpointOperations, endpointSolutionManager);
    }

    @Override
    public String serviceName() {
        return "inquiry";
    }
}
