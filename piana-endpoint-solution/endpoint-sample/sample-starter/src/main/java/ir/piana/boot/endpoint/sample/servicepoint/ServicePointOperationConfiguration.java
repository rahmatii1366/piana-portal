package ir.piana.boot.endpoint.sample.servicepoint;

import ir.piana.boot.endpoint.operation.EndpointOperation;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBRequest;
import ir.piana.boot.endpoint.sample.service.sampleb.SampleBResponse;
import ir.piana.boot.endpoint.sample.servicepoint.vehicleinfo.InsuranceVehicleServicePointOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ServicePointOperationConfiguration {
    @Bean
    public InsuranceVehicleServicePointOperation insuranceVehicleInfoServicePoint(
            List<EndpointOperation<SampleBRequest, SampleBResponse>> endpointOperations
    ) {
        return new InsuranceVehicleServicePointOperation(null);
    }
}
