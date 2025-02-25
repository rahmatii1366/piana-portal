package ir.piana.boot.endpoint.sample.servicepoint;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.operation.EndpointOperation;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBRequest;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBResponse;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoRequest;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoResponse;
import ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo.InsuranceVehicleServicePointOperation;
import ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo.SampleServicePointOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ServicePointOperationConfiguration {
    @Bean
    public SampleServicePointOperation sampleServicePointOperation(
            List<EndpointOperation<SampleBRequest, SampleBResponse>> endpointOperations,
            EndpointSolutionManager endpointSolutionManager
    ) {
        return new SampleServicePointOperation(endpointOperations, endpointSolutionManager);
    }

    @Bean
    public InsuranceVehicleServicePointOperation insuranceVehicleInfoServicePoint(
            List<EndpointOperation<VehicleInfoRequest, VehicleInfoResponse>> endpointOperations,
            EndpointSolutionManager endpointSolutionManager
    ) {
        return new InsuranceVehicleServicePointOperation(endpointOperations, endpointSolutionManager);
    }
}
