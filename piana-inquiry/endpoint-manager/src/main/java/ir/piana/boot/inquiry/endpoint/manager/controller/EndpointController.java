package ir.piana.boot.inquiry.endpoint.manager.controller;

import ir.piana.boot.inquiry.common.dto.ResponseDto;
import ir.piana.boot.inquiry.common.httpclient.HttpClientProperties;
import ir.piana.boot.inquiry.endpoint.manager.scheduler.EndpointDBScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

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
