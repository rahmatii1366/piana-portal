package ir.piana.boot.inquiry.common.httpclient;

import java.util.List;

public record HttpClientProperties(
        long id,
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
        long updateOn,
        List<String> tlsVersions
        ) {
}
