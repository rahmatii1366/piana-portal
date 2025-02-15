package ir.piana.boot.utils.endpointhandler;

import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

public final class RestClientRequestHandler<R> {
    private final ApplicationContext applicationContext;

    public RestClientRequestHandler(
            ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public final RestClient.ResponseSpec retrieve(RestRequest restRequest) {
        RestClient restClient = applicationContext.getBean(RestClient.class);
        return restRequest.retrieve(restClient);
    }

    public final R retrieve(
            RestRequest restRequest, RestClient.RequestHeadersSpec.ExchangeFunction<R> exchangeFunction) {
        RestClient restClient = applicationContext.getBean(RestClient.class);
        return restRequest.exchange(restClient, exchangeFunction);
    }

    /*public R apply(RestRequest restRequest) {
        return (R) restRequest.retrieve(restClient);
    }*/
}
