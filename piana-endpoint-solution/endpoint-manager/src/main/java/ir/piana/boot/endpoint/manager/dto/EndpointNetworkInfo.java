package ir.piana.boot.endpoint.manager.dto;

import jakarta.websocket.Endpoint;

import java.sql.Timestamp;

public record EndpointNetworkInfo(
        long id,
        Endpoint endpoint,
        boolean isDebugMode,
        boolean isSecure,
        String host,
        int port,
        String baseUrl,
        int soTimeout,
        int connectionTimeout,
        int socketTimeout,
        int timeToLive,
        String poolReusePolicy,
        String poolConcurrencyPolicy,
        String trustStore,
        String trustStorePassword,
        String tlsVersions,
        boolean disabled,
        Timestamp createOn
) {
}
