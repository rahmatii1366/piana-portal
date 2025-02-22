package ir.piana.boot.endpoint.core.dto;

import java.util.List;

public record ChangedServicePointCollectionDto(
        List<ChangedServicePointDto> changedServicePoints) {
}
