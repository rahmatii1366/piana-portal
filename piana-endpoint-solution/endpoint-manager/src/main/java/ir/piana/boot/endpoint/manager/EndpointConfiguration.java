package ir.piana.boot.endpoint.manager;

import ir.piana.boot.endpoint.core.manager.EndpointSolutionManager;
import ir.piana.boot.endpoint.core.manager.info.*;
import ir.piana.boot.endpoint.data.tables.*;
import ir.piana.boot.endpoint.data.tables.daos.*;
import ir.piana.boot.utils.flyway.FlywaySure;
import ir.piana.boot.utils.flyway.PrimaryFluentConfiguration;
import ir.piana.boot.utils.restclientconfigurable.HttpEndpointDto;
import ir.piana.boot.utils.restclientconfigurable.RestClientBuilderUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jooq.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
//@ConditionalOnProperty(prefix = "piana.tools.endpoint-solution", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EndpointConfiguration {
    @Component
    @ConfigurationProperties(prefix = "piana.tools.endpoint-solution")
    @Getter
    @Setter
    public class EndpointsProperties {
        private List<String> endpoints;
        private List<String> services;

        @PostConstruct
        public void init() {
            System.out.println();
        }
    }

    private Map<Long, EndpointInfo> endpointMap(
            EndpointDao endpointDao,
            EndpointsProperties endpointsProperties) {
        SelectConditionStep<Record3<Long, String, String>> where = endpointDao.ctx().select(
                        Endpoint.ENDPOINT.ID,
                        Endpoint.ENDPOINT.NAME,
                        Endpoint.ENDPOINT.DESCRIPTION
                )
                .from(Endpoint.ENDPOINT)
                .where(Endpoint.ENDPOINT.LOGICAL_DELETION.eq(false));
        if (endpointsProperties.endpoints != null && !endpointsProperties.endpoints.isEmpty()) {
            where.and(Endpoint.ENDPOINT.NAME.in(endpointsProperties.endpoints));
        }
        List<EndpointInfo> fetch = where.fetch(rec -> new EndpointInfo(
                rec.get(Endpoint.ENDPOINT.ID),
                rec.get(Endpoint.ENDPOINT.NAME),
                rec.get(Endpoint.ENDPOINT.DESCRIPTION)
        ));
        return fetch.stream()
                .collect(Collectors.toMap(EndpointInfo::id, Function.identity()));
    }

    private Map<Long, ServiceInfo> serviceMapByServiceId(
            ServiceDao serviceDao,
            EndpointsProperties endpointsProperties) {
        SelectConditionStep<Record3<Long, String, String>> where = serviceDao.ctx().select(
                        Service.SERVICE.ID,
                        Service.SERVICE.NAME,
                        Service.SERVICE.DESCRIPTION
                )
                .from(Service.SERVICE)
                .where(Service.SERVICE.LOGICAL_DELETION.eq(false));
        if (endpointsProperties.services != null && !endpointsProperties.services.isEmpty()) {
            where.and(Service.SERVICE.NAME.in(endpointsProperties.services));
        }
        List<ServiceInfo> fetch = where.fetch(rec -> new ServiceInfo(
                rec.get(Service.SERVICE.ID),
                rec.get(Service.SERVICE.NAME),
                rec.get(Service.SERVICE.DESCRIPTION)
        ));
        return fetch.stream()
                .collect(Collectors.toMap(ServiceInfo::id, Function.identity()));
    }

    private Map<String, ServiceInfo> serviceMapByServiceName(
            Map<Long, ServiceInfo> serviceMapByServiceId) {
        return serviceMapByServiceId.values().stream()
                .collect(Collectors.toMap(ServiceInfo::name, Function.identity()));
    }

    private Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap(
            EndpointNetworkDao endpointNetworkDao,
            Map<Long, EndpointInfo> endpointInfoMap
    ) {
        SelectSeekStep1<Record16<Long, Long, String, Integer, String, Boolean, Boolean, Integer, Integer, Integer,
                Integer, String, String, String, String, String>, Long> rec16s = endpointNetworkDao.ctx()
                .select(
                        EndpointNetwork.ENDPOINT_NETWORK.ID,
                        EndpointNetwork.ENDPOINT_NETWORK.ENDPOINT_ID,
                        EndpointNetwork.ENDPOINT_NETWORK.HOST,
                        EndpointNetwork.ENDPOINT_NETWORK.PORT,
                        EndpointNetwork.ENDPOINT_NETWORK.BASE_URL,
                        EndpointNetwork.ENDPOINT_NETWORK.IS_SECURE,
                        EndpointNetwork.ENDPOINT_NETWORK.IS_DEBUG_MODE,
                        EndpointNetwork.ENDPOINT_NETWORK.CONNECTION_TIMEOUT,
                        EndpointNetwork.ENDPOINT_NETWORK.SO_TIMEOUT,
                        EndpointNetwork.ENDPOINT_NETWORK.SOCKET_TIMEOUT,
                        EndpointNetwork.ENDPOINT_NETWORK.TIME_TO_LIVE,
                        EndpointNetwork.ENDPOINT_NETWORK.POOL_CONCURRENCY_POLICY,
                        EndpointNetwork.ENDPOINT_NETWORK.POOL_REUSE_POLICY,
                        EndpointNetwork.ENDPOINT_NETWORK.TLS_VERSIONS,
                        EndpointNetwork.ENDPOINT_NETWORK.TRUST_STORE,
                        EndpointNetwork.ENDPOINT_NETWORK.TRUST_STORE_PASSWORD
                )
                .from(EndpointNetwork.ENDPOINT_NETWORK)
                .where(EndpointNetwork.ENDPOINT_NETWORK.ENDPOINT_ID.in(endpointInfoMap.keySet()))
                .and(EndpointNetwork.ENDPOINT_NETWORK.LOGICAL_DELETION.eq(false))
                .orderBy(EndpointNetwork.ENDPOINT_NETWORK.ENDPOINT_ID);
        List<EndpointNetworkInfo> fetch = rec16s.fetch(rec -> new EndpointNetworkInfo(
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.ID),
                endpointInfoMap.get(rec.get(EndpointNetwork.ENDPOINT_NETWORK.ENDPOINT_ID)),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.IS_DEBUG_MODE),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.IS_SECURE),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.HOST),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.PORT),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.BASE_URL),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.SO_TIMEOUT),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.CONNECTION_TIMEOUT),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.SOCKET_TIMEOUT),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.TIME_TO_LIVE),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.POOL_REUSE_POLICY),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.POOL_CONCURRENCY_POLICY),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.TRUST_STORE),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.TRUST_STORE_PASSWORD),
                rec.get(EndpointNetwork.ENDPOINT_NETWORK.TLS_VERSIONS)
        ));
        Map<EndpointInfo, List<EndpointNetworkInfo>> collect = fetch.stream()
                .collect(Collectors.groupingBy(EndpointNetworkInfo::endpoint));
        return collect;
    }

    private Map<Long, EndpointNetworkInfo> endpointNetworkMap(
            Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap
    ) {
        Map<Long, EndpointNetworkInfo> collect = endpointToEndpointNetworkMap.values().stream().flatMap(List::stream)
                .collect(Collectors.toMap(EndpointNetworkInfo::id, Function.identity()));
        return collect;
    }

    private Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap(
            EndpointApiDao endpointApiDao,
            Map<Long, EndpointInfo> endpointInfoMap,
            Map<Long, ServiceInfo> serviceInfoMap
    ) {
        SelectSeekStep1<Record9<Long, Long, Long, String, String, String, String, String, String>,
                Long> record9s = endpointApiDao.ctx()
                .select(
                        EndpointApi.ENDPOINT_API.ID,
                        EndpointApi.ENDPOINT_API.ENDPOINT_ID,
                        EndpointApi.ENDPOINT_API.SERVICE_ID,
                        EndpointApi.ENDPOINT_API.METHOD,
                        EndpointApi.ENDPOINT_API.URL,
                        EndpointApi.ENDPOINT_API.ACCEPTABLE_QUERY_PARAMS,
                        EndpointApi.ENDPOINT_API.ACCEPTABLE_PATH_PARAMS,
                        EndpointApi.ENDPOINT_API.ACCEPTABLE_HEADER_KEYS,
                        EndpointApi.ENDPOINT_API.DESCRIPTION
                )
                .from(EndpointApi.ENDPOINT_API)
                .where(EndpointApi.ENDPOINT_API.ENDPOINT_ID.in(endpointInfoMap.keySet()))
                .and(EndpointApi.ENDPOINT_API.LOGICAL_DELETION.eq(false))
                .orderBy(EndpointApi.ENDPOINT_API.ENDPOINT_ID);

        List<EndpointApiInfo> fetch = record9s.fetch(rec -> {
                    String queryParams = rec.get(EndpointApi.ENDPOINT_API.ACCEPTABLE_QUERY_PARAMS);
                    String pathParams = rec.get(EndpointApi.ENDPOINT_API.ACCEPTABLE_PATH_PARAMS);
                    String headers = rec.get(EndpointApi.ENDPOINT_API.ACCEPTABLE_HEADER_KEYS);

                    return new EndpointApiInfo(
                            rec.get(EndpointApi.ENDPOINT_API.ID),
                            endpointInfoMap.get(rec.get(EndpointApi.ENDPOINT_API.ENDPOINT_ID)),
                            serviceInfoMap.get(rec.get(EndpointApi.ENDPOINT_API.SERVICE_ID)),
                            rec.get(EndpointApi.ENDPOINT_API.METHOD),
                            rec.get(EndpointApi.ENDPOINT_API.URL),
                            queryParams == null ? Collections.EMPTY_LIST : Arrays.asList(queryParams.split(",")),
                            pathParams == null ? Collections.EMPTY_LIST : Arrays.asList(pathParams.split(",")),
                            headers == null ? Collections.EMPTY_LIST : Arrays.asList(headers.split(",")),
                            rec.get(EndpointApi.ENDPOINT_API.DESCRIPTION));
                }
        );
        Map<EndpointInfo, List<EndpointApiInfo>> collect = fetch.stream()
                .collect(Collectors.groupingBy(EndpointApiInfo::endpoint));
        return collect;
    }

    private Map<ServiceInfo, List<EndpointApiInfo>> serviceToEndpointApiMap(
            Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap
    ) {
        Map<ServiceInfo, List<EndpointApiInfo>> collect = endpointToEndpointApiMap.values()
                .stream().flatMap(List::stream)
                .collect(Collectors.groupingBy(EndpointApiInfo::service));
        return collect;
    }

    private Map<EndpointInfo, List<EndpointClientInfo>> endpointToEndpointClientMap(
            EndpointClientDao endpointClientDao,
            Map<Long, EndpointInfo> endpointInfoMap,
            Map<Long, EndpointNetworkInfo> endpointNetworkMap
    ) {
        SelectSeekStep1<Record14<Long, Long, Long, String, String, String, Integer, Integer, Integer, Integer, Integer,
                Integer, Integer, Integer>, Long> record14s = endpointClientDao.ctx()
                .select(
                        EndpointClient.ENDPOINT_CLIENT.ID,
                        EndpointClient.ENDPOINT_CLIENT.ENDPOINT_ID,
                        EndpointClient.ENDPOINT_CLIENT.ENDPOINT_NETWORK_ID,
                        EndpointClient.ENDPOINT_CLIENT.CLIENT_ID,
                        EndpointClient.ENDPOINT_CLIENT.SECRET_KEY,
                        EndpointClient.ENDPOINT_CLIENT.JSON_CREDENTIAL,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_TPS,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_MINUTE,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_HOUR,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_DAY,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_WEEK,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_MONTH,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_YEAR,
                        EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_TOTAL
                )
                .from(EndpointClient.ENDPOINT_CLIENT)
                .where(EndpointClient.ENDPOINT_CLIENT.ENDPOINT_ID.in(endpointInfoMap.keySet()))
                .and(EndpointClient.ENDPOINT_CLIENT.LOGICAL_DELETION.eq(false))
                .orderBy(EndpointClient.ENDPOINT_CLIENT.ENDPOINT_ID);
        List<EndpointClientInfo> fetch = record14s.fetch(rec -> new EndpointClientInfo(
                rec.get(EndpointClient.ENDPOINT_CLIENT.ID),
                endpointInfoMap.get(rec.get(EndpointClient.ENDPOINT_CLIENT.ENDPOINT_ID)),
                endpointNetworkMap.get(rec.get(EndpointClient.ENDPOINT_CLIENT.ENDPOINT_NETWORK_ID)),
                rec.get(EndpointClient.ENDPOINT_CLIENT.CLIENT_ID),
                rec.get(EndpointClient.ENDPOINT_CLIENT.SECRET_KEY),
                rec.get(EndpointClient.ENDPOINT_CLIENT.JSON_CREDENTIAL),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_TPS),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_MINUTE),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_HOUR),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_DAY),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_WEEK),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_MONTH),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_YEAR),
                rec.get(EndpointClient.ENDPOINT_CLIENT.LIMITATION_IN_TOTAL)
        ));
        Map<EndpointInfo, List<EndpointClientInfo>> collect = fetch.stream()
                .collect(Collectors.groupingBy(EndpointClientInfo::endpoint));
        return collect;
    }

    private RestClient createRestClient(
            RestClientBuilderUtils restClientBuilderUtils,
            EndpointClientInfo endpointClientInfo) {
        EndpointNetworkInfo endpointNetworkInfo = endpointClientInfo.endpointNetwork();
        RestClient restClient = restClientBuilderUtils.createRestClient(new HttpEndpointDto(
                endpointNetworkInfo.id(),
                endpointNetworkInfo.endpoint().name(),
                endpointNetworkInfo.isDebugMode(),
                endpointNetworkInfo.isSecure(),
                endpointNetworkInfo.host(),
                endpointNetworkInfo.port(),
                endpointNetworkInfo.baseUrl(),
                (long) endpointNetworkInfo.soTimeout(),
                (long) endpointNetworkInfo.connectionTimeout(),
                (long) endpointNetworkInfo.socketTimeout(),
                (long) endpointNetworkInfo.timeToLive(),
                endpointNetworkInfo.poolReusePolicy(),
                endpointNetworkInfo.poolConcurrencyPolicy(),
                endpointNetworkInfo.trustStore(),
                endpointNetworkInfo.trustStorePassword(),
                0,
                Arrays.asList(endpointNetworkInfo.tlsVersions().split(","))
        ));
        return restClient;
    }

    private Map<Long, MerchantInfo> merchantIdToMerchantInfoMap(
            MerchantDao merchantDao
    ) {
        List<MerchantInfo> fetch = merchantDao.ctx()
                .select(
                        Merchant.MERCHANT.ID,
                        Merchant.MERCHANT.NAME,
                        Merchant.MERCHANT.DESCRIPTION
                )
                .from(Merchant.MERCHANT)
                .where(Merchant.MERCHANT.LOGICAL_DELETION.eq(false))
                .orderBy(Merchant.MERCHANT.ID).fetch(rec -> new MerchantInfo(
                        rec.get(Merchant.MERCHANT.ID),
                        rec.get(Merchant.MERCHANT.NAME),
                        rec.get(Merchant.MERCHANT.DESCRIPTION)
                ));
        Map<Long, MerchantInfo> collect = fetch.stream()
                .collect(Collectors.toMap(MerchantInfo::id, Function.identity()));
        return collect;
    }

    private Map<Long, Map<Long, List<EndpointInfo>>> serviceIdMapToMerchantIdMapToEndpoints(
            ServiceOrderGroupDao serviceOrderGroupDao,
            Map<Long, EndpointInfo> endpointInfoMap
    ) {
        List<ServiceOrderGroupInfo> fetch = serviceOrderGroupDao.ctx()
                .select(
                        ServiceOrderGroup.SERVICE_ORDER_GROUP.ID,
                        ServiceOrderGroup.SERVICE_ORDER_GROUP.SERVICE_ID,
                        ServiceOrderGroup.SERVICE_ORDER_GROUP.MERCHANT_ID
                )
                .from(ServiceOrderGroup.SERVICE_ORDER_GROUP)
                .where(ServiceOrderGroup.SERVICE_ORDER_GROUP.LOGICAL_DELETION.eq(false))
                .orderBy(ServiceOrderGroup.SERVICE_ORDER_GROUP.ID)
                .fetch(rec -> {
                    Long serviceOrderGroupId = rec.get(ServiceOrderGroup.SERVICE_ORDER_GROUP.ID);
                    List<EndpointInfo> orderedEndpointInfo = serviceOrderGroupDao.ctx().select(
                                    ServiceOrder.SERVICE_ORDER.ENDPOINT_ID,
                                    ServiceOrder.SERVICE_ORDER.ORDERS)
                            .from(ServiceOrder.SERVICE_ORDER)
                            .where(ServiceOrder.SERVICE_ORDER.LOGICAL_DELETION.eq(false))
                            .and(ServiceOrder.SERVICE_ORDER.ENDPOINT_ID.eq(serviceOrderGroupId))
                            .orderBy(ServiceOrder.SERVICE_ORDER.ORDERS.asc())
                            .fetch(rec2 -> {
                                Long endpointId = rec2.get(ServiceOrder.SERVICE_ORDER.ENDPOINT_ID);
                                return endpointInfoMap.get(endpointId);
                            });

                    return new ServiceOrderGroupInfo(
                            rec.get(ServiceOrderGroup.SERVICE_ORDER_GROUP.ID),
                            rec.get(ServiceOrderGroup.SERVICE_ORDER_GROUP.SERVICE_ID),
                            rec.get(ServiceOrderGroup.SERVICE_ORDER_GROUP.MERCHANT_ID),
                            orderedEndpointInfo
                    );
                });
        Map<Long, Map<Long, List<EndpointInfo>>> collect = fetch.stream()
                .collect(Collectors.groupingBy(ServiceOrderGroupInfo::serviceId,
                        Collectors.toMap(ServiceOrderGroupInfo::merchantId, ServiceOrderGroupInfo::endpoints)));
        return collect;
    }

    private Map<Long, Map<Long, List<EndpointClientInfo>>> endpointIdMapToMerchantIdMapToEndpointClientsByOrder(
            EndpointOrderGroupDao endpointOrderGroupDao,
            Map<Long, EndpointClientInfo> endpointClientIdMapToEndpointClient) {
        List<EndpointOrderGroupInfo> fetch = endpointOrderGroupDao.ctx()
                .select(
                        EndpointOrderGroup.ENDPOINT_ORDER_GROUP.ID,
                        EndpointOrderGroup.ENDPOINT_ORDER_GROUP.ENDPOINT_ID,
                        EndpointOrderGroup.ENDPOINT_ORDER_GROUP.MERCHANT_ID
                )
                .from(EndpointOrderGroup.ENDPOINT_ORDER_GROUP)
                .where(EndpointOrderGroup.ENDPOINT_ORDER_GROUP.LOGICAL_DELETION.eq(false))
                .orderBy(EndpointOrderGroup.ENDPOINT_ORDER_GROUP.ID)
                .fetch(rec -> {
                    Long endpointOrderGroupId = rec.get(EndpointOrderGroup.ENDPOINT_ORDER_GROUP.ID);
                    List<EndpointClientInfo> endpointClientInfoList = endpointOrderGroupDao.ctx().select(
                                    EndpointOrder.ENDPOINT_ORDER.ENDPOINT_CLIENT_ID,
                                    EndpointOrder.ENDPOINT_ORDER.ORDERS)
                            .from(EndpointOrder.ENDPOINT_ORDER)
                            .where(EndpointOrder.ENDPOINT_ORDER.LOGICAL_DELETION.eq(false))
                            .and(EndpointOrder.ENDPOINT_ORDER.ENDPOINT_ORDER_GROUP_ID.eq(endpointOrderGroupId))
                            .orderBy(EndpointOrder.ENDPOINT_ORDER.ORDERS.asc())
                            .fetch(rec2 -> {
                                Long endpointClientId = rec2.get(EndpointOrder.ENDPOINT_ORDER.ENDPOINT_CLIENT_ID);
                                return endpointClientIdMapToEndpointClient.get(endpointClientId);
                            });

                    return new EndpointOrderGroupInfo(
                            endpointOrderGroupId,
                            rec.get(EndpointOrderGroup.ENDPOINT_ORDER_GROUP.ENDPOINT_ID),
                            rec.get(EndpointOrderGroup.ENDPOINT_ORDER_GROUP.MERCHANT_ID),
                            endpointClientInfoList
                    );
                });
        Map<Long, Map<Long, List<EndpointClientInfo>>> collect = fetch.stream()
                .collect(Collectors.groupingBy(EndpointOrderGroupInfo::endpointId,
                        Collectors.toMap(EndpointOrderGroupInfo::merchantId, EndpointOrderGroupInfo::endpointClients)));
        return collect;
    }

    @Bean
    public PrimaryFluentConfiguration endpointsFluentConfiguration() {
        FluentConfiguration endpoints = Flyway.configure().schemas("endpoints")
                .locations("db/migration/endpoint")
                .baselineOnMigrate(true)
                .baselineVersion("1.0");
        return new PrimaryFluentConfiguration(endpoints);
    }

    @Bean
    public EndpointSolutionManager endpointSolutionManager(
            FlywaySure flywaySure,
            RestClientBuilderUtils restClientBuilderUtils,
            EndpointsProperties endpointsProperties,
            EndpointDao endpointDao,
            ServiceDao serviceDao,
            EndpointNetworkDao endpointNetworkDao,
            EndpointApiDao endpointApiDao,
            EndpointClientDao endpointClientDao,
            MerchantDao merchantDao,
            ServiceOrderGroupDao serviceOrderGroupDao,
            EndpointOrderGroupDao endpointOrderGroupDao) {
        Map<Long, EndpointInfo> endpointInfoMap = endpointMap(endpointDao, endpointsProperties);
        Map<Long, ServiceInfo> serviceInfoMapByServiceId = serviceMapByServiceId(serviceDao, endpointsProperties);
        Map<String, ServiceInfo> serviceInfoMapByServiceName = serviceMapByServiceName(serviceInfoMapByServiceId);
        Map<EndpointInfo, List<EndpointNetworkInfo>> endpointToEndpointNetworkMap = endpointToEndpointNetworkMap(
                endpointNetworkDao, endpointInfoMap);
        Map<Long, EndpointNetworkInfo> endpointNetworkInfoMap = endpointNetworkMap(endpointToEndpointNetworkMap);
        Map<EndpointInfo, List<EndpointApiInfo>> endpointToEndpointApiMap = endpointToEndpointApiMap(
                endpointApiDao, endpointInfoMap, serviceInfoMapByServiceId);
        Map<ServiceInfo, List<EndpointApiInfo>> serviceToEndpointApiMap = serviceToEndpointApiMap(endpointToEndpointApiMap);
        Map<EndpointInfo, List<EndpointClientInfo>> endpointToEndpointClientMap = endpointToEndpointClientMap(
                endpointClientDao, endpointInfoMap, endpointNetworkInfoMap);
        Map<Long, EndpointClientInfo> endpointClientIdMapToEndpointClient = null;
        Map<EndpointClientInfo, RestClient> endpointClientToRestClientMap = endpointToEndpointClientMap.values().stream()
                .flatMap(List::stream).map(endpointClient -> new AbstractMap.SimpleEntry<>(
                        endpointClient, createRestClient(restClientBuilderUtils, endpointClient)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, MerchantInfo> merchantIdToMerchantMap = merchantIdToMerchantInfoMap(merchantDao);
        Map<Long, Map<Long, List<EndpointInfo>>> serviceIdMapToMerchantIdMapToEndpoints =
                serviceIdMapToMerchantIdMapToEndpoints(serviceOrderGroupDao, endpointInfoMap);
        Map<Long, Map<Long, List<EndpointClientInfo>>> endpointIdMapToMerchantIdMapToEndpointClients =
                endpointIdMapToMerchantIdMapToEndpointClientsByOrder(
                        endpointOrderGroupDao, endpointClientIdMapToEndpointClient);
        return new EndpointSolutionManagerImpl(
                endpointInfoMap,
                serviceInfoMapByServiceId,
                serviceInfoMapByServiceName,
                endpointToEndpointNetworkMap,
                endpointNetworkInfoMap,
                endpointToEndpointApiMap,
                serviceToEndpointApiMap,
                endpointClientIdMapToEndpointClient,
                endpointToEndpointClientMap,
                endpointClientToRestClientMap,
                merchantIdToMerchantMap,
                serviceIdMapToMerchantIdMapToEndpoints,
                endpointIdMapToMerchantIdMapToEndpointClients);
    }
}
