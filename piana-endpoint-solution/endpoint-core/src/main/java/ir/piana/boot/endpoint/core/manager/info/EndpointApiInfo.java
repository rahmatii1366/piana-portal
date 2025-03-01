package ir.piana.boot.endpoint.core.manager.info;

import java.util.List;

public record EndpointApiInfo(
        long id,
        EndpointInfo endpoint,
        ServiceInfo service,
        String method,
        String url,
        List<String> acceptableQueryParams,
        List<String> acceptablePathParams,
        List<String> acceptableHeaderKeys,
        String description
) {
}
