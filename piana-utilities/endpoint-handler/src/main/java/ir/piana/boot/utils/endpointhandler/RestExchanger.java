package ir.piana.boot.utils.endpointhandler;

import org.springframework.web.client.RestClient;

import java.util.function.Function;

public interface RestExchanger<R> extends Function<RestClient, R> {
    R exchange(RestClient restClient);
}
