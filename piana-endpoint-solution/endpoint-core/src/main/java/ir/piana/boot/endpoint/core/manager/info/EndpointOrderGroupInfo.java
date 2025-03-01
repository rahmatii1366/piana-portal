package ir.piana.boot.endpoint.core.manager.info;

import java.util.List;

public record EndpointOrderGroupInfo(
        long id,
        long endpointId,
        long merchantId,
        List<EndpointClientInfo> endpointClients) {
}
