package ir.piana.boot.endpoint.core.manager.info;

public record EndpointNetworkInfo(
        long id,
        EndpointInfo endpoint,
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
        String tlsVersions
) {
}
