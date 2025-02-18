package ir.piana.boot.endpoint.dto;

import java.util.List;

public record ChangedServicePointDto(
        String name,
        boolean deleted,
        boolean added,
        List<ChangedEndpointDto> changedEndpoints
) {
}
