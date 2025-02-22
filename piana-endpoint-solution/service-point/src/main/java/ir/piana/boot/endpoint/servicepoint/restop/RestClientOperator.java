package ir.piana.boot.endpoint.servicepoint.restop;

import org.springframework.web.client.RestClient;

import java.util.function.Function;

public interface RestClientOperator extends Function<RestClient, RestClientResponse> {
}
