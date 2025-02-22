package ir.piana.boot.endpoint.manager.dto;

public record EndpointClientInfo(
        long id,
        EndpointInfo endpoint,
        EndpointNetworkInfo endpointNetwork,
        String clientId,
        String secretKey,
        String jsonCredential,
        int limitationInTps,
        int limitationInMinute,
        int limitationInHour,
        int limitationInDay,
        int limitationInWeek,
        int limitationInMonth,
        int limitationInYear,
        int limitationInTotal,
        int disabled,
        int createOn
) {
}
