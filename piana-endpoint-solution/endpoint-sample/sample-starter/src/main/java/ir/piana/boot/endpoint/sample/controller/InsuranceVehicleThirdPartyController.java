package ir.piana.boot.endpoint.sample.controller;

import ir.piana.boot.endpoint.manager.EndpointsProperties;
import ir.piana.boot.endpoint.sample.service.VehicleInfoResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/insurance/vehicle/third-party")
@RequiredArgsConstructor
public class InsuranceVehicleThirdPartyController {
    private final EndpointsProperties endpointsProperties;

    @PostConstruct
    public void postInit() {
        System.out.println();
    }

    @GetMapping(path = "inquiry-vehicle-info-by-vin-and-production-year")
    public ResponseEntity<VehicleInfoResponse> vehicleInfoResponseResponse() {
        return ResponseEntity.ok(new VehicleInfoResponse("132"));
    }
}
