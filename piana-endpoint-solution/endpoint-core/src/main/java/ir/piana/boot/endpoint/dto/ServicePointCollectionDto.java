package ir.piana.boot.endpoint.dto;

import java.util.List;

public record ServicePointCollectionDto(
        List<ServicePointDto> servicePoints
) {
}
