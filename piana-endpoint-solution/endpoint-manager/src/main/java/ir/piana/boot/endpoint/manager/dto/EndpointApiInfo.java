package ir.piana.boot.endpoint.manager.dto;

import java.sql.Timestamp;

public record EndpointApiInfo(
        long id,
        EndpointInfo endpoint,
        ServiceInfo service,
        String method,
        String url,
        String description
) {
}
