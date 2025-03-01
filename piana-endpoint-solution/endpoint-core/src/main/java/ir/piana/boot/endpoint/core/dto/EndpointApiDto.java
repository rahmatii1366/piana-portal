package ir.piana.boot.endpoint.core.dto;

public record EndpointApiDto(
        long endpointId,
        long serviceId,
        String method,
        String url,
        String acceptableQueryParams,
        String acceptablePathParams,
        String acceptableHeaderKey,
        String description
) {
}
