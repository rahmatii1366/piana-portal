package ir.piana.dev.inquiry.publisher.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ir.piana.boot.inquiry.common.dto.ResponseDto;
import ir.piana.boot.inquiry.thirdparty.insurance.vehicle.InsuranceVehicleThirdPartyService;
import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/inquiry/insurance/vehicle/third-party")
public class TabanController {
    private final InsuranceVehicleThirdPartyService service;

    public TabanController(InsuranceVehicleThirdPartyService service) {
        this.service = service;
    }

    @GetMapping("search-by-vin")
    public ResponseEntity<ResponseDto> token(
            @RequestParam("vin") String vin,
            @RequestParam("productionYear") String productionYear) {
        if (true)
            throw ApiExceptionService.customApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "test.error", "test.error", "myError");
        JsonNode jsonNode = service.getByVin(vin, productionYear);
//            JsonNode jsonNode = service.getByVin("NAS831100L5877148", "1399");
        return ResponseEntity.ok(new ResponseDto(jsonNode));
    }
}
