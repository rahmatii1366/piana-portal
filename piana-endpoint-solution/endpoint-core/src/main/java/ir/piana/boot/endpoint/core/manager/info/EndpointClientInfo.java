package ir.piana.boot.endpoint.core.manager.info;

public record EndpointClientInfo(
        long id,
        EndpointInfo endpoint,
        EndpointNetworkInfo endpointNetwork,
        String clientId,
        String secretKey,
        String jsonCredential,
        Integer limitationInTps,
        Integer limitationInMinute,
        Integer limitationInHour,
        Integer limitationInDay,
        Integer limitationInWeek,
        Integer limitationInMonth,
        Integer limitationInYear,
        Integer limitationInTotal
) {
}
