package ir.piana.boot.endpoint.manager;

import ir.piana.boot.endpoint.manager.dto.EndpointClientInfo;
import org.springframework.web.client.RestClient;

public class RestClientManagerImpl implements RestClientManager {
    private RestClient restClient;
    private EndpointClientInfo endpointClientInfo;


    @Override
    public void validateAuth() {

    }

    @Override
    public void invalidateAuth() {

    }

    @Override
    public void doRequest() {

    }
}
