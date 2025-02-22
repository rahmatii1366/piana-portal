package ir.piana.boot.endpoint.manager.dto;

import java.sql.Timestamp;

public record EndpointApiInfo(
        long id,
        EndpointInfo endpoint,
        String method,
        String url,
        String description,
        boolean disabled,
        Timestamp create_on
) {
}
