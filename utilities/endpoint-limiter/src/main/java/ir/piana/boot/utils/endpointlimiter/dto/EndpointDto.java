package ir.piana.boot.utils.endpointlimiter.dto;

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
                limitationDto
        );
    }
}
