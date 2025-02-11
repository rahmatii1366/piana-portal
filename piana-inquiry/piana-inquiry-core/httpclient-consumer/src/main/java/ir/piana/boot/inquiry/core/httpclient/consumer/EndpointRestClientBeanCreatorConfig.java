package ir.piana.boot.inquiry.core.httpclient.consumer;

import ir.piana.boot.inquiry.common.httpclient.HttpClientProperties;
import ir.piana.boot.inquiry.common.httpclient.RestClientBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EndpointRestClientBeanCreatorConfig {
    private final RestClientBuilderUtils restClientBuilderUtils;

    private final Map<String, HttpClientProperties> endpointMap = new LinkedHashMap<>();

    public EndpointRestClientBeanCreatorConfig(
            RestClientBuilderUtils restClientBuilderUtils) {
        this.restClientBuilderUtils = restClientBuilderUtils;
    }

    public void refresh(List<HttpClientProperties> httpClientPropertiesList) {
        List<String> newClientNames = new ArrayList<>();
        httpClientPropertiesList.forEach(httpClientProperties -> {
                    newClientNames.add(httpClientProperties.name());
                    if (!endpointMap.containsKey(httpClientProperties.name())) {
                        RestClient restClient = restClientBuilderUtils.registerRestClient(
                                httpClientProperties);
                        endpointMap.put(httpClientProperties.name(), httpClientProperties);
                    } else {
                        HttpClientProperties existed = endpointMap.get(httpClientProperties.name());
                        if (existed.updateOn() != httpClientProperties.updateOn()) {
                            RestClient restClient = restClientBuilderUtils.registerRestClient(
                                    httpClientProperties);
                            endpointMap.put(httpClientProperties.name(), httpClientProperties);
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
