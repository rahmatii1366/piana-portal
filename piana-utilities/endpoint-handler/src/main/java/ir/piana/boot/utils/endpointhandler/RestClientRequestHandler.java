package ir.piana.boot.utils.endpointhandler;

import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

public final class RestClientRequestHandler {
    private final ApplicationContext applicationContext;

    public RestClientRequestHandler(
            ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public final <R> R exchange(
            String restClientName, RestExchanger<R> restExchanger) {
        return restExchanger.exchange(
                applicationContext.getBean(restClientName, RestClient.class));
    }

    public final RestClient.ResponseSpec retrieve(
            String restClientName, RestRetriever restRequest) {
        return restRequest.retrieve(
                applicationContext.getBean(restClientName, RestClient.class));
    }

    public final RestClient.ResponseSpec retrieve(RestRequest restRequest) {
        return restRequest.retrieve(applicationContext);
    }

    public final <R> R exchange(
            RestRequest restRequest,
            RestClient.RequestHeadersSpec.ExchangeFunction<R> exchangeFunction) {
        return restRequest.exchange(applicationContext, exchangeFunction);
    }
}
