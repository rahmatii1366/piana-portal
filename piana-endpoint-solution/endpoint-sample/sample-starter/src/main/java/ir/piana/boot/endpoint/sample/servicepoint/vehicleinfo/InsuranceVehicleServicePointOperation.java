package ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo;

import ir.piana.boot.endpoint.operation.EndpointOperation;
import ir.piana.boot.endpoint.sample.service.VehicleInfoRequest;
import ir.piana.boot.endpoint.sample.service.VehicleInfoResponse;
import ir.piana.boot.endpoint.servicepoint.ServicePointOperation;
import ir.piana.boot.endpoint.servicepoint.ServicePointRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InsuranceVehicleServicePointOperation
        implements ServicePointOperation<VehicleInfoRequest, VehicleInfoResponse> {
    private final List<EndpointOperation<VehicleInfoRequest, VehicleInfoResponse>> endpointOperations;

    @Override
    public VehicleInfoResponse apply(ServicePointRequest<VehicleInfoRequest> vehicleInfoRequestServicePointRequest) {
        return null;
    }
}
