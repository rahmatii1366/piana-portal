package ir.piana.boot.endpoint.manager.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import ir.piana.boot.endpoint.dto.EndpointDto;
import ir.piana.boot.endpoint.dto.EndpointLimitationDto;
import ir.piana.boot.endpoint.dto.ServicePointCollectionDto;
import ir.piana.boot.endpoint.dto.ServicePointDto;
import ir.piana.boot.endpoint.data.tables.Endpoint;
import ir.piana.boot.endpoint.data.tables.EndpointLimitation;
import ir.piana.boot.endpoint.data.tables.ServicePoint;
import ir.piana.boot.endpoint.data.tables.daos.EndpointDao;
import ir.piana.boot.endpoint.data.tables.daos.ServicePointDao;
import ir.piana.boot.utils.jedisutils.JedisPool;
import ir.piana.boot.utils.natsclient.NatsConfig;
import ir.piana.boot.utils.restclientconfigurable.HttpEndpointDto;
import ir.piana.boot.utils.scheduler.FixedIntervalScheduler;
import org.apache.hc.core5.http.ssl.TLS;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "piana.scheduler.endpoint")
public class EndpointDBScheduler extends FixedIntervalScheduler {
    private final ServicePointDao servicePointDao;
    private final EndpointDao endpointDao;
    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final JedisPool jedisPool;

    private List<HttpEndpointDto> httpClientProperties;

    public EndpointDBScheduler(
            ServicePointDao servicePointDao,
            EndpointDao endpointDao,
            @Lazy Connection connection,
            ObjectMapper objectMapper,
            JedisPool jedisPool) {
        this.servicePointDao = servicePointDao;
        this.endpointDao = endpointDao;
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.jedisPool = jedisPool;
    }

    @Override
    public void run() {
        List<ServicePointDto> tempServicePoints = this.servicePointDao.ctx().select(
                        ServicePoint.SERVICE_POINT.ID,
                        ServicePoint.SERVICE_POINT.NAME,
                        ServicePoint.SERVICE_POINT.DESCRIPTION)
                .from(ServicePoint.SERVICE_POINT)
                .where(ServicePoint.SERVICE_POINT.DISABLED.eq(false))
                .fetch(record -> {
                    ServicePointDto servicePointDto = new ServicePointDto(
                            record.get(ServicePoint.SERVICE_POINT.ID),
                            record.get(ServicePoint.SERVICE_POINT.NAME),
                            record.get(ServicePoint.SERVICE_POINT.DESCRIPTION),
                            null
                    );
                    return servicePointDto;
                });

        ServicePointCollectionDto servicePointCollection = new ServicePointCollectionDto(tempServicePoints.stream().map(servicePointDto -> {
            List<EndpointDto> tempEndpoints = this.endpointDao.ctx().select(
                            Endpoint.ENDPOINT.ID,
                            Endpoint.ENDPOINT.EXECUTION_ORDER,
                            Endpoint.ENDPOINT.NAME,
                            Endpoint.ENDPOINT.IS_DEBUG_MODE,
                            Endpoint.ENDPOINT.IS_SECURE,
                            Endpoint.ENDPOINT.IS_SECURE,
                            Endpoint.ENDPOINT.HOST,
                            Endpoint.ENDPOINT.PORT,
                            Endpoint.ENDPOINT.BASE_URL,
                            Endpoint.ENDPOINT.SO_TIMEOUT,
                            Endpoint.ENDPOINT.CONNECTION_TIMEOUT,
                            Endpoint.ENDPOINT.SOCKET_TIMEOUT,
                            Endpoint.ENDPOINT.TIME_TO_LIVE,
                            Endpoint.ENDPOINT.POOL_REUSE_POLICY,
                            Endpoint.ENDPOINT.POOL_CONCURRENCY_POLICY,
                            Endpoint.ENDPOINT.TRUST_STORE,
                            Endpoint.ENDPOINT.TRUST_STORE_PASSWORD,
                            Endpoint.ENDPOINT.UPDATE_ON,
                            Endpoint.ENDPOINT.TLS_VERSIONS)
                    .from(Endpoint.ENDPOINT)
                    .where(Endpoint.ENDPOINT.SERVICE_POINT_ID.eq(servicePointDto.id()))
                    .and(Endpoint.ENDPOINT.DISABLED.eq(false))
                    .orderBy(Endpoint.ENDPOINT.EXECUTION_ORDER.asc())
                    .fetch(record ->
                            new EndpointDto(
                                    record.get(Endpoint.ENDPOINT.ID),
                                    record.get(Endpoint.ENDPOINT.EXECUTION_ORDER),
                                    record.get(Endpoint.ENDPOINT.NAME),
                                    record.get(Endpoint.ENDPOINT.IS_DEBUG_MODE),
                                    record.get(Endpoint.ENDPOINT.IS_SECURE),
                                    record.get(Endpoint.ENDPOINT.HOST),
                                    record.get(Endpoint.ENDPOINT.PORT),
                                    record.get(Endpoint.ENDPOINT.BASE_URL),
                                    record.get(Endpoint.ENDPOINT.SO_TIMEOUT).longValue(),
                                    record.get(Endpoint.ENDPOINT.CONNECTION_TIMEOUT).longValue(),
                                    record.get(Endpoint.ENDPOINT.SOCKET_TIMEOUT).longValue(),
                                    record.get(Endpoint.ENDPOINT.TIME_TO_LIVE).longValue(),
                                    record.get(Endpoint.ENDPOINT.POOL_REUSE_POLICY),
                                    record.get(Endpoint.ENDPOINT.POOL_CONCURRENCY_POLICY),
                                    record.get(Endpoint.ENDPOINT.TRUST_STORE),
                                    record.get(Endpoint.ENDPOINT.TRUST_STORE_PASSWORD),
                                    Arrays.stream(Optional.ofNullable(record.get(Endpoint.ENDPOINT.TLS_VERSIONS))
                                            .orElse(TLS.V_1_3.name()).split(",")).toList(),
                                    record.get(Endpoint.ENDPOINT.UPDATE_ON).toEpochSecond(ZoneOffset.UTC),
                                    null)
                    );

            List<EndpointDto> endpoints = tempEndpoints.stream().map(endpointDto -> {
                List<EndpointLimitationDto> endpointLimitationDto = this.endpointDao.ctx().select()
                        .from(EndpointLimitation.ENDPOINT_LIMITATION)
                        .where(EndpointLimitation.ENDPOINT_LIMITATION.ENDPOINT_ID.eq(endpointDto.id()))
                        .orderBy(EndpointLimitation.ENDPOINT_LIMITATION.CREATE_ON.asc())
                        .limit(1)
                        .offset(0)
                        .fetch(record ->
                                new EndpointLimitationDto(
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.START_AT),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.EXPIRE_AT),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_TPS),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_MINUTE),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_HOUR),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_DAY),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_WEEK),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_MONTH),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_YEAR),
                                        record.get(EndpointLimitation.ENDPOINT_LIMITATION.LIMITATION_IN_PERIOD))
                        );

                return new EndpointDto(endpointDto, endpointLimitationDto.getFirst());
            }).toList();
            return new ServicePointDto(servicePointDto, endpoints);
        }).toList());

        try {
            String body = objectMapper.writeValueAsString(servicePointCollection);
            /*String body = objectMapper.writeValueAsString(
                    Endpoints.builder().httpClientProperties(httpClientProperties).build());*/
            jedisPool.setKey("endpoints", body);
            connection.publish("piana.inquiry.endpoint.refreshed", body.getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        /*clients.stream().map(client -> {
            try {
                PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create();
                if (client.isSecure()) {
                    List<TLS> tlsVersions = null;
                    tlsVersions = client.tlsVersions().stream().map(TLS::valueOf).toList();
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

                RestClient restClient = null;
                RestClient.Builder restClientBuilder = RestClient.builder()
                        .requestFactory(requestFactory)
                        .baseUrl((client.isSecure() ? "https://" : "http://") +
                                client.host() + ":" + client.port() + "/" +
                                (client.baseUrl() != null ? client.baseUrl() + "/" : ""));

                if (client.debugMode) {
                    restClientBuilder.requestInterceptor((request, body, execution) -> {
                        logRequest(log, request, body);
                        var response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
                        logResponse(log, request, response);
                        response.reset();
                        return response;
                    });
                }

                restClient = restClientBuilder.build();
                applicationContext.registerBean(client.name(), RestClient.class, () -> restClient);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });*/
    }

    public List<HttpEndpointDto> getEndpoints() {
        return httpClientProperties;
    }
}
