package ir.piana.boot.inquiry.core.httpclient.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.boot.inquiry.common.httpclient.Endpoints;
import ir.piana.boot.inquiry.common.httpclient.HttpClientProperties;
import ir.piana.boot.inquiry.common.httpclient.RestClientBuilderUtils;
import ir.piana.boot.utils.errorprocessor.ApiException;
import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import ir.piana.boot.utils.jedisutils.JedisPool;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EndpointRestClientBeanCreatorConfig {
    private final RestClientBuilderUtils restClientBuilderUtils;
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    private final Map<String, HttpClientProperties> endpointMap = new LinkedHashMap<>();

    public EndpointRestClientBeanCreatorConfig(
            RestClientBuilderUtils restClientBuilderUtils,
            ObjectMapper objectMapper,
            JedisPool jedisPool) {
        this.restClientBuilderUtils = restClientBuilderUtils;
        this.objectMapper = objectMapper;
        this.jedisPool = jedisPool;
    }

    @PostConstruct
    public void onPostConstruct() {
        String endpointsString = jedisPool.getKey("endpoints");
        if (endpointsString != null && !endpointsString.isEmpty()) {
            try {
                Endpoints endpoints = objectMapper.readValue(endpointsString.getBytes(), Endpoints.class);
                refresh(endpoints.getHttpClientProperties());
            } catch (IOException e) {
                ApiException apiException = ApiExceptionService.customApiException(
                        HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                log.error(apiException.getMessage(), apiException);
            }
        }
    }

    public synchronized void refresh(List<HttpClientProperties> httpClientPropertiesList) {
        List<String> newClientNames = new ArrayList<>();
        httpClientPropertiesList.forEach(httpClientProperties -> {
                    newClientNames.add(httpClientProperties.getName());
                    if (!endpointMap.containsKey(httpClientProperties.getName())) {
                        RestClient restClient = restClientBuilderUtils.registerRestClient(
                                httpClientProperties);
                        endpointMap.put(httpClientProperties.getName(), httpClientProperties);
                    } else {
                        HttpClientProperties existed = endpointMap.get(httpClientProperties.getName());
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
}
