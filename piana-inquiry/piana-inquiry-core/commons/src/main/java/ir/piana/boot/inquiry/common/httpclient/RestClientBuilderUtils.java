package ir.piana.boot.inquiry.common.httpclient;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RestClientBuilderUtils {
    private static Logger logger = LoggerFactory.getLogger(InternalRestClientBeanCreatorConfig.class);
    private final GenericWebApplicationContext applicationContext;
    private final ResourceLoader resourceLoader;

    public RestClientBuilderUtils(
            GenericWebApplicationContext applicationContext, ResourceLoader resourceLoader) {
        this.applicationContext = applicationContext;
        this.resourceLoader = resourceLoader;
    }

    public void removeRestClientBean(String beanName) {
        applicationContext.removeBeanDefinition(beanName);
        logger.info("client by name {} removed", beanName);
    }

    public RestClient registerRestClient(
            HttpClientProperties httpClientProperties) {
        try {
            PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create();
            if (httpClientProperties.isSecure()) {
                List<TLS> tlsVersions = null;
                List<String> tlsVersionStrings = httpClientProperties.getTlsVersions();
                if (tlsVersionStrings != null && !tlsVersionStrings.isEmpty()) {
                    tlsVersions = tlsVersionStrings.stream().map(TLS::valueOf).toList();
                } else {
                    tlsVersions = List.of(TLS.V_1_3);
                }
                if (httpClientProperties.getTrustStore() == null || httpClientProperties.getTrustStore().isEmpty()) {
                    builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContexts.custom()
                                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                                    .build())
                            .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                            .build());
                } else {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    if (httpClientProperties.getTrustStore().startsWith("classpath:")) {
                        Resource resource = resourceLoader.getResource(httpClientProperties.getTrustStore());
                        ks.load(resource.getInputStream(), httpClientProperties.getTrustStorePassword().toCharArray());
                    } else {
                        File trustFile = new File(httpClientProperties.getTrustStore());
                        ks.load(new FileInputStream(trustFile), httpClientProperties.getTrustStorePassword().toCharArray());
                    }

                    builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContexts.custom()
                                    .loadTrustMaterial(
                                            ks,
                                            (X509Certificate[] chain, String authType) -> true)
                                    .build())
                            .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                            .build());
                    //ToDo load from trustStore
                }
            }

            builder.setDefaultSocketConfig(SocketConfig.custom()
                            .setSoTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.getSoTimeout()).orElse(20L)))
                            .build())
                    .setPoolConcurrencyPolicy(Optional.ofNullable(httpClientProperties.getPoolConcurrencyPolicy())
                            .map(PoolConcurrencyPolicy::valueOf).orElse(PoolConcurrencyPolicy.STRICT))
                    .setConnPoolPolicy(Optional.ofNullable(httpClientProperties.getPoolReusePolicy())
                            .map(PoolReusePolicy::valueOf).orElse(PoolReusePolicy.LIFO))
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setSocketTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.getSocketTimeout()).orElse(30L)))
                            .setConnectTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.getConnectionTimeout()).orElse(30L)))
                            .setTimeToLive(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.getTimeToLive()).orElse(600L)))
                            .build())
                    .build();
            Logger log = LoggerFactory.getLogger(httpClientProperties.getName());
            final CloseableHttpClient httpClient = HttpClientBuilder
                    .create()
                    .setConnectionManager(builder.build())
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            RestClient.Builder restClientBuilder = RestClient.builder()
                    .requestFactory(requestFactory)
                    .baseUrl((httpClientProperties.isSecure() ? "https://" : "http://") +
                            httpClientProperties.getHost() + ":" + httpClientProperties.getPort() + "/" +
                            (httpClientProperties.getBaseUrl() != null ? httpClientProperties.getBaseUrl() + "/" : ""));
            if (httpClientProperties.isDebugMode()) {
                restClientBuilder.requestInterceptor((request, body, execution) -> {
                    logRequest(log, request, body);
                    var response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
                    logResponse(log, request, response);
                    response.reset();
                    return response;
                });
            }
            RestClient restClient = restClientBuilder.build();
            if (applicationContext.containsBean(httpClientProperties.getName())) {
//                BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
//                BeanDefinition newBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(httpClientProperties.name(), RestClient.class, () -> restClient).getBeanDefinition();
                //this is needed if u want to revert your bean changes back to how it was
                //BeanDefinition oldBeanDefinition = beanRegistry.getBeanDefinition(httpClientProperties.name());
//                beanRegistry.registerBeanDefinition(httpClientProperties.name(), newBeanDefinition);
                applicationContext.removeBeanDefinition(httpClientProperties.getName());
            }

            applicationContext.registerBean(httpClientProperties.getName(), RestClient.class, () -> restClient);

            logger.info("client by name {} registered", httpClientProperties.getName());
            return restClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*static RestClient registerRestClient(
            GenericWebApplicationContext applicationContext,
            ResourceLoader resourceLoader,
            HttpClientProperties httpClientProperties) {
        try {
            PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create();
            if (httpClientProperties.isSecure()) {
                List<TLS> tlsVersions = null;
                List<String> tlsVersionStrings = httpClientProperties.tlsVersions();
                if (tlsVersionStrings != null && !tlsVersionStrings.isEmpty()) {
                    tlsVersions = tlsVersionStrings.stream().map(TLS::valueOf).toList();
                } else {
                    tlsVersions = List.of(TLS.V_1_3);
                }
                if (httpClientProperties.trustStore() == null || httpClientProperties.trustStore().isEmpty()) {
                    builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContexts.custom()
                                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                                    .build())
                            .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                            .build());
                } else {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    if (httpClientProperties.trustStore().startsWith("classpath:")) {
                        Resource resource = resourceLoader.getResource(httpClientProperties.trustStore());
                        ks.load(resource.getInputStream(), httpClientProperties.trustStorePassword().toCharArray());
                    } else {
                        File trustFile = new File(httpClientProperties.trustStore());
                        ks.load(new FileInputStream(trustFile), httpClientProperties.trustStorePassword().toCharArray());
                    }

                    builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContexts.custom()
                                    .loadTrustMaterial(
                                            ks,
                                            (X509Certificate[] chain, String authType) -> true)
                                    .build())
                            .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                            .build());
                    //ToDo load from trustStore
                }
            }

            builder.setDefaultSocketConfig(SocketConfig.custom()
                            .setSoTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.soTimeout()).orElse(20L)))
                            .build())
                    .setPoolConcurrencyPolicy(Optional.ofNullable(httpClientProperties.poolConcurrencyPolicy())
                            .map(PoolConcurrencyPolicy::valueOf).orElse(PoolConcurrencyPolicy.STRICT))
                    .setConnPoolPolicy(Optional.ofNullable(httpClientProperties.poolReusePolicy())
                            .map(PoolReusePolicy::valueOf).orElse(PoolReusePolicy.LIFO))
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setSocketTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.socketTimeout()).orElse(30L)))
                            .setConnectTimeout(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.connectionTimeout()).orElse(30L)))
                            .setTimeToLive(Timeout.ofSeconds(
                                    Optional.ofNullable(httpClientProperties.timeToLive()).orElse(600L)))
                            .build())
                    .build();
            Logger log = LoggerFactory.getLogger(httpClientProperties.name());
            final CloseableHttpClient httpClient = HttpClientBuilder
                    .create()
                    .setConnectionManager(builder.build())
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            RestClient.Builder restClientBuilder = RestClient.builder()
                    .requestFactory(requestFactory)
                    .baseUrl((httpClientProperties.isSecure() ? "https://" : "http://") +
                            httpClientProperties.host() + ":" + httpClientProperties.port() + "/" +
                            (httpClientProperties.baseUrl() != null ? httpClientProperties.baseUrl() + "/" : ""));
            if (httpClientProperties.isDebugMode()) {
                restClientBuilder.requestInterceptor((request, body, execution) -> {
                    logRequest(log, request, body);
                    var response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
                    logResponse(log, request, response);
                    response.reset();
                    return response;
                });
            }
            RestClient restClient = restClientBuilder.build();
            applicationContext.registerBean(httpClientProperties.name(), RestClient.class, () -> restClient);
            return restClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    private void logRequest(Logger log, HttpRequest request, byte[] body) throws IOException {
        log.info("Request: {} {}", request.getMethod(), request.getURI());
        logHeaders(log, request.getHeaders());
        if (body != null && body.length > 0) {
            log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(Logger log, HttpRequest request, ClientHttpResponse response) throws IOException {
        log.info("Response status: {}", response.getStatusCode().value());
        logHeaders(log, response.getHeaders());
        byte[] responseBody = response.getBody().readAllBytes();
        if (responseBody.length > 0) {
            log.info("Response body: {}", new String(responseBody, StandardCharsets.UTF_8));
        }
    }

    private void logHeaders(Logger log, HttpHeaders headers) throws IOException {
        log.info("Headers: {}", headers.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining(";")));
    }

    /*@PostConstruct
    public void onPostConstruct() {
        *//*PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultTlsConfig(TlsConfig.custom()
                        .setHandshakeTimeout(Timeout.ofSeconds(30))
                        .setSupportedProtocols(TLS.V_1_2, TLS.V_1_3)
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofMinutes(1))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMinutes(1))
                        .setConnectTimeout(Timeout.ofMinutes(1))
                        .setTimeToLive(TimeValue.ofMinutes(10))
                        .build())
                .build();*//*

        clients.clients.forEach(client -> {
            try {
                PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create();
                if (client.isSecure()) {
                    List<TLS> tlsVersions = null;
                    List<String> tlsVersionStrings = client.tlsVersions();
                    if (tlsVersionStrings != null && !tlsVersionStrings.isEmpty()) {
                        tlsVersions = tlsVersionStrings.stream().map(TLS::valueOf).toList();
                    } else {
                        tlsVersions = List.of(TLS.V_1_3);
                    }
                    if (client.trustStore() == null || client.trustStore().isEmpty()) {
                        builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(SSLContexts.custom()
                                        .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                                        .build())
                                .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                            .build());
                    } else {
                        KeyStore ks = KeyStore.getInstance("JKS");
                        if (client.trustStore().startsWith("classpath:")) {
                            Resource resource = resourceLoader.getResource(client.trustStore());
                            ks.load(resource.getInputStream(), client.trustStorePassword().toCharArray());
                        } else {
                            File trustFile = new File(client.trustStore());
                            ks.load(new FileInputStream(trustFile), client.trustStorePassword().toCharArray());
                        }

                        builder.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(SSLContexts.custom()
                                        .loadTrustMaterial(
                                                ks,
                                                (X509Certificate[] chain, String authType) -> true)
                                        .build())
                                .setTlsVersions(tlsVersions.toArray(new TLS[0]))
                                .build());
                        //ToDo load from trustStore
                    }
                }

                builder.setDefaultSocketConfig(SocketConfig.custom()
                                .setSoTimeout(Timeout.ofSeconds(
                                        Optional.ofNullable(client.soTimeout()).orElse(20L)))
                                .build())
                        .setPoolConcurrencyPolicy(Optional.ofNullable(client.poolConcurrencyPolicy())
                                .map(PoolConcurrencyPolicy::valueOf).orElse(PoolConcurrencyPolicy.STRICT))
                        .setConnPoolPolicy(Optional.ofNullable(client.poolReusePolicy())
                                .map(PoolReusePolicy::valueOf).orElse(PoolReusePolicy.LIFO))
                        .setDefaultConnectionConfig(ConnectionConfig.custom()
                                .setSocketTimeout(Timeout.ofSeconds(
                                        Optional.ofNullable(client.socketTimeout()).orElse(30L)))
                                .setConnectTimeout(Timeout.ofSeconds(
                                        Optional.ofNullable(client.connectionTimeout()).orElse(30L)))
                                .setTimeToLive(Timeout.ofSeconds(
                                        Optional.ofNullable(client.timeToLive()).orElse(600L)))
                                .build())
                        .build();
                Logger log = LoggerFactory.getLogger(client.name());
                final CloseableHttpClient httpClient = HttpClientBuilder
                        .create()
                        .setConnectionManager(builder.build())
                        .build();

                HttpComponentsClientHttpRequestFactory requestFactory =
                        new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);

                RestClient restClient = RestClient.builder()
                        .requestFactory(requestFactory)
                        .baseUrl((client.isSecure() ? "https://" : "http://") +
                                client.host() + ":" + client.port() + "/" +
                                (client.baseUrl() != null ? client.baseUrl() + "/" : ""))
                        .requestInterceptor((request, body, execution) -> {
                            logRequest(log, request, body);
                            var response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
                            logResponse(log, request, response);
                            response.reset();
                            return response;
                        })
                        .build();
                applicationContext.registerBean(client.name(), RestClient.class, () -> restClient);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }*/
}
