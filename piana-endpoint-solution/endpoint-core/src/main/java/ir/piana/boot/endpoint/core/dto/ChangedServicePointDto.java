package ir.piana.boot.endpoint.core.dto;

import java.util.List;

public record ChangedServicePointDto(
        String name,
        boolean deleted,
        boolean added,
        List<ChangedEndpointDto> changedEndpoints
) {
}
