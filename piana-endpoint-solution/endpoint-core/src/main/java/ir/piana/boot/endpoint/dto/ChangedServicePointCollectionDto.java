package ir.piana.boot.endpoint.dto;

import java.util.List;

public record ChangedServicePointCollectionDto(
        List<ChangedServicePointDto> changedServicePoints) {

}
