package ir.piana.boot.endpoint.servicepoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.boot.endpoint.core.dto.*;
import ir.piana.boot.endpoint.core.mapper.ToHttpEndpointDto;
import ir.piana.boot.utils.jedisutils.JedisPool;
import ir.piana.boot.utils.natsclient.MessageHandler;
import ir.piana.boot.utils.restclientconfigurable.HttpEndpointDto;
import ir.piana.boot.utils.restclientconfigurable.RestClientBuilderUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RestClientOperationHandleableBeanCreator
        implements MessageHandler<ChangedServicePointCollectionDto, Object> {
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;
    private final GenericApplicationContext applicationContext;
    private final RestClientBuilderUtils restClientBuilderUtils;

    private final Map<String, HttpEndpointDto> endpointMap = new LinkedHashMap<>();

    public RestClientOperationHandleableBeanCreator(
            JedisPool jedisPool,
            ObjectMapper objectMapper,
            GenericApplicationContext applicationContext,
            RestClientBuilderUtils restClientBuilderUtils) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        this.restClientBuilderUtils = restClientBuilderUtils;
    }

    @PostConstruct
    public void onPostConstruct(
    ) throws JsonProcessingException {
        String endpoints = jedisPool.getKey("servicePointCollections");
        if (endpoints != null && !endpoints.isEmpty()) {
            ServicePointCollectionDto servicePointCollectionDto = objectMapper.readValue(
                    endpoints, ServicePointCollectionDto.class);
            registerServicePointCollectionBean(servicePointCollectionDto);
            List<HttpEndpointDto> list = servicePointCollectionDto.servicePoints().stream()
                    .map(servicePointDto -> ToHttpEndpointDto.map(
                            servicePointDto.name(), servicePointDto.endpoints()))
                    .flatMap(List::stream)
                    .toList();
            refreshRestClients(list);
            this.restClientOperationHandleables.forEach(
                    restClientOperationHandleable -> {
                        try {
                            ServicePointDto servicePointDto = servicePointCollectionDto.servicePoints().stream().filter(
                                            dto -> dto.name().equalsIgnoreCase(
                                                    restClientOperationHandleable.servicePointName()))
                                    .findFirst().orElseThrow(() -> new RuntimeException());
                            EndpointDto endpointDto = servicePointDto.endpoints().stream().filter(
                                    dto -> dto.name().equalsIgnoreCase(
                                            restClientOperationHandleable.endpointName())
                            ).findFirst().orElseThrow(() -> new RuntimeException());

                            EndpointLimitationDto limitationDto = endpointDto.limitationDto();
                            restClientOperationHandleable.refreshLimitation(limitationDto);
                        } catch (Exception e) {
                            log.error("refresh limitation failed", e);
                        }
                    });
        }
    }

    @Override
    public String subject() {
        return "piana.inquiry.endpoint.refreshed";
    }

    @Override
    public Class<ChangedServicePointCollectionDto> dtoType() {
        return ChangedServicePointCollectionDto.class;
    }


    @Override
    public Object apply(ChangedServicePointCollectionDto changedServicePointCollectionDto) {
        /**
         * ToDo should be changed to {@link #onChangeServicePoints(ChangedServicePointCollectionDto)}
         */
        onChangeServicePoints();
        return null;
    }


    /**
     * ToDO It should be developed to improve performance based on what has actually changed.
     * @param changedServicePointCollectionDto
     */
    private void onChangeServicePoints(ChangedServicePointCollectionDto changedServicePointCollectionDto) {
    }

    @Deprecated
    private void onChangeServicePoints() {
        try {
            String servicePointCollectionsString = jedisPool.getKey("servicePointCollections");
            ServicePointCollectionDto servicePointCollectionDto = objectMapper.readValue(
                    servicePointCollectionsString, ServicePointCollectionDto.class);
            registerServicePointCollectionBean(servicePointCollectionDto);
            List<HttpEndpointDto> list = servicePointCollectionDto.servicePoints().stream()
                    .map(servicePointDto -> ToHttpEndpointDto.map(
                            servicePointDto.name(), servicePointDto.endpoints()))
                    .flatMap(List::stream)
                    .toList();
            refreshRestClients(list);
//        registerServicePointOperations(servicePointCollectionDto, restClientOperationHandleables);
            this.restClientOperationHandleables.forEach(
                    restClientOperationHandleable -> {
                        try {
                            restClientOperationHandleable.refreshLimitation(servicePointCollectionDto);
                        } catch (Exception e) {
                            log.error("refresh limitation failed", e);
                        }
                    });
        } catch (Exception e) {
            //ToDo It needs to be improved
            log.error("changed client endpoints faced with error", e);
        }
    }

    /*public synchronized void refresh(ServicePointCollectionDto servicePointCollectionDto) {
        List<String> newClientNames = new ArrayList<>();
        httpClientPropertiesList.forEach(httpClientProperties -> {
                    newClientNames.add(httpClientProperties.getName());
                    if (!endpointMap.containsKey(httpClientProperties.getName())) {
                        RestClient restClient = restClientBuilderUtils.registerRestClient(
                                httpClientProperties);
                        endpointMap.put(httpClientProperties.getName(), httpClientProperties);
                    } else {
                        HttpEndpointDto existed = endpointMap.get(httpClientProperties.getName());
                        if (existed.getUpdateOn() != httpClientProperties.getUpdateOn()) {
                            RestClient restClient = restClientBuilderUtils.registerRestClient(
                                    httpClientProperties);
                            endpointMap.put(httpClientProperties.getName(), httpClientProperties);
                        }
                    }
                }
        );

        List<String> removableClients = endpointMap.keySet().stream()
                .filter(k -> !newClientNames.contains(k))
                .toList();

        removableClients.forEach(clientName -> {
            restClientBuilderUtils.removeRestClientBean(clientName);
            endpointMap.remove(clientName);
        });
    }*/

    public synchronized void refreshRestClients(List<HttpEndpointDto> httpClientPropertiesList) {
        List<String> newClientNames = new ArrayList<>();
        httpClientPropertiesList.forEach(httpClientProperties -> {
                    newClientNames.add(httpClientProperties.getName());
                    if (!endpointMap.containsKey(httpClientProperties.getName())) {
                        RestClient restClient = restClientBuilderUtils.registerRestClient(
                                httpClientProperties);
                        endpointMap.put(httpClientProperties.getName(), httpClientProperties);
                    } else {
                        HttpEndpointDto existed = endpointMap.get(httpClientProperties.getName());
                        if (existed.getUpdateOn() != httpClientProperties.getUpdateOn()) {
                            RestClient restClient = restClientBuilderUtils.registerRestClient(
                                    httpClientProperties);
                            endpointMap.put(httpClientProperties.getName(), httpClientProperties);
                        }
                    }
                }
        );

        List<String> removableClients = endpointMap.keySet().stream()
                .filter(k -> !newClientNames.contains(k))
                .toList();

        removableClients.forEach(clientName -> {
            restClientBuilderUtils.removeRestClientBean(clientName);
            endpointMap.remove(clientName);
        });
    }

    public void registerServicePointCollectionBean(ServicePointCollectionDto servicePointCollectionDto) {
        if (applicationContext.containsBean("ServicePointCollectionDto")) {
            applicationContext.removeBeanDefinition("ServicePointCollectionDto");
        }

        applicationContext.registerBean(
                "ServicePointCollectionDto", ServicePointCollectionDto.class, () -> servicePointCollectionDto);
    }

    @Override
    public RestClientOperationHandleable provide(String servicePointName, String endpointName) {
        return null;
    }

    /*public void registerServicePointOperations(
            ServicePointCollectionDto servicePointCollectionDto,
            List<RestClientOperationHandleable> restClientOperationHandleableList) {
        for (ServicePointDto servicePoint : servicePointCollectionDto.servicePoints()) {
            if (applicationContext.containsBean(servicePoint.name())) {
                applicationContext.removeBeanDefinition(servicePoint.name());
            }

            ServicePointOperation servicePointOperation = new ServicePointOperationImpl(
                    applicationContext, servicePoint, restClientOperationHandleables.stream()
                    .filter(restClientOperationHandleable ->
                            restClientOperationHandleable.servicePointName().equalsIgnoreCase(servicePoint.name()))
                    .sorted((o1, o2) ->
                        o1.executionOrder() > o2.executionOrder() ? 1 : -1
                    )
                    .toList());
            applicationContext.registerBean(
                    servicePoint.name(), ServicePointOperation.class, () -> servicePointOperation);
        }
    }*/
}
