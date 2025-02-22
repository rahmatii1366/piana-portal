package ir.piana.boot.endpoint.core.dto;

import java.util.List;

public record ServicePointCollectionDto(
        List<ServicePointDto> servicePoints
) {
}
