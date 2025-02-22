package ir.piana.boot.endpoint.core.dto;

import java.util.List;

public record EndpointDto(
        long id,
        int executionOrder,
        String name,
        boolean isDebugMode,
        boolean isSecure,
        String host,
        int port,
        String baseUrl,
        Long soTimeout,
        Long connectionTimeout,
        Long socketTimeout,
        Long timeToLive,
        String poolReusePolicy, // LIFO, FIFO
        String poolConcurrencyPolicy,
        String trustStore,
        String trustStorePassword,
        List<String> tlsVersions,
        long createOn,
        EndpointLimitationDto limitationDto
) {
    public EndpointDto(EndpointDto endpointDto, EndpointLimitationDto limitationDto) {
        this(endpointDto.id,
                endpointDto.executionOrder,
                endpointDto.name,
                endpointDto.isDebugMode,
                endpointDto.isSecure,
                endpointDto.host,
                endpointDto.port,
                endpointDto.baseUrl,
                endpointDto.soTimeout,
                endpointDto.connectionTimeout,
                endpointDto.socketTimeout,
                endpointDto.timeToLive,
                endpointDto.poolReusePolicy, // LIFO, FIF
                endpointDto.poolConcurrencyPolicy,
                endpointDto.trustStore,
                endpointDto.trustStorePassword,
                endpointDto.tlsVersions,
                endpointDto.createOn,
                limitationDto
        );
    }

    @Override
    public int hashCode() {
        int prime = 19;
        int hash = prime;
        hash = prime * hash + executionOrder;
        hash = prime * hash + (name != null ? name.hashCode() : 0);
        hash = prime * hash + (isDebugMode ? 1 : 0);
        hash = prime * hash + (isSecure ? 1 : 0);
        hash = prime * hash + (host != null ? host.hashCode() : 0);
        hash = prime * hash + port;
        hash = prime * hash + (baseUrl != null ? baseUrl.hashCode() : 0);
        hash = prime * hash + (soTimeout != null ? soTimeout.intValue() : 0);
        hash = prime * hash + (connectionTimeout != null ? connectionTimeout.intValue() : 0);
        hash = prime * hash + (socketTimeout != null ? socketTimeout.intValue() : 0);
        hash = prime * hash + (timeToLive != null ? timeToLive.intValue() : 0);
        hash = prime * hash + (poolReusePolicy != null ? poolReusePolicy.hashCode() : 0);
        hash = prime * hash + (poolConcurrencyPolicy != null ? poolConcurrencyPolicy.hashCode() : 0);
        hash = prime * hash + (trustStore != null ? trustStore.hashCode() : 0);
        hash = prime * hash + (trustStorePassword != null ? trustStorePassword.hashCode() : 0);
        if (tlsVersions != null && !tlsVersions.isEmpty()) {
            hash = prime * hash + String.join("", tlsVersions).hashCode();
        }
        return hash;
    }
}
