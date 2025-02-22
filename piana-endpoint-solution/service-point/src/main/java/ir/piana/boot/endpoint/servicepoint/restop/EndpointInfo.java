package ir.piana.boot.endpoint.servicepoint.restop;

public record EndpointInfo(
        long servicePointId,
        String servicePointName,
        long endpointId,
        String endpointName
        ) {
}
