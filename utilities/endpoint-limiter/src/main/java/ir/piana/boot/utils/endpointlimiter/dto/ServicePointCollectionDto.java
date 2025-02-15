package ir.piana.boot.utils.endpointlimiter.dto;

import java.util.List;

public record ServicePointCollectionDto(
        List<ServicePointDto> servicePoints
) {
}
