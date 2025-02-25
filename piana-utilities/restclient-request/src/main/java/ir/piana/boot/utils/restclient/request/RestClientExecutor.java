package ir.piana.boot.utils.restclient.request;

import org.springframework.web.client.RestClient;

public interface RestClientExecutor {
    RestClient.ResponseSpec request(RestRequest request);
}
