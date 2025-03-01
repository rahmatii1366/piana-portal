package ir.piana.boot.endpoint.core.manager.info;

import java.util.List;

public record ServiceOrderGroupInfo(
        long id,
        long serviceId,
        long merchantId,
        List<EndpointInfo> endpoints) {
}
