package ir.piana.boot.endpoint.core.manager.info;

public record EndpointApiInfo(
        long id,
        EndpointInfo endpoint,
        ServiceInfo service,
        String method,
        String url,
        String description
) {
}
