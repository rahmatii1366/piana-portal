package ir.piana.boot.endpoint.core.dto;

import java.util.List;

public record ServicePointDto(
        long id,
        String name,
        String description,
        List<EndpointDto> endpoints
) {
    public ServicePointDto(ServicePointDto servicePointDto, List<EndpointDto> endpoints) {
        this(servicePointDto.id, servicePointDto.name, servicePointDto.description, endpoints);
    }
}
