package ir.piana.boot.endpoint.manager.controller;

import ir.piana.boot.endpoint.manager.scheduler.EndpointDBScheduler;
import ir.piana.boot.utils.errorprocessor.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/internal/endpoint")
public class EndpointController {
    private final EndpointDBScheduler endpointDBScheduler;

    public EndpointController(EndpointDBScheduler endpointDBScheduler) {
        this.endpointDBScheduler = endpointDBScheduler;
    }

    @GetMapping(path = "enable-list")
    public ResponseEntity<ResponseDto> getEnabledEndpoints() {
        return ResponseEntity.ok(new ResponseDto(endpointDBScheduler.getEndpoints()));
    }
}
