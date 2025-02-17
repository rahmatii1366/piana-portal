package ir.piana.dev.inquiry.publisher.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance.VehicleThirdPartyInsuranceServicePoint;
import ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance.dto.VehicleThirdPartyInsuranceRequest;
import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import ir.piana.boot.utils.errorprocessor.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/inquiry/insurance/vehicle/third-party")
public class VehicleThirdPartyInsuranceController {
//    private final InsuranceVehicleThirdPartyService service;
    private final VehicleThirdPartyInsuranceServicePoint<JsonNode> servicePointOperation;

    public VehicleThirdPartyInsuranceController(
            VehicleThirdPartyInsuranceServicePoint<JsonNode> servicePointOperation) {
        this.servicePointOperation = servicePointOperation;
    }

    @GetMapping("search-by-vin")
    public ResponseEntity<ResponseDto> token(
            @RequestParam("vin") String vin,
            @RequestParam("productionYear") String productionYear) {
       /* if (true)
            throw ApiExceptionService.customApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "test.error", "test.error", "myError");*/
        JsonNode jsonNode = servicePointOperation.apply(new VehicleThirdPartyInsuranceRequest(
                vin, productionYear
        ));
        return ResponseEntity.ok(new ResponseDto(jsonNode));
    }
}
