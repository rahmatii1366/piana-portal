package ir.piana.boot.utils.endpointhandler;

import org.springframework.web.client.RestClient;

import java.util.function.Function;

public interface RestRetriever extends Function<RestClient, RestClient.ResponseSpec> {
    RestClient.ResponseSpec retrieve(RestClient restClient);
}
