package ir.piana.boot.endpoint.sample.service.sampleb;

import ir.piana.boot.endpoint.operation.EndpointOperationException;
import ir.piana.boot.utils.restclient.request.RestClientExecutor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SampleBProviderOne extends SampleBServiceOperation {
    @PostConstruct
    public void onPostConstruct() {
        System.out.println();
    }

    @Override
    public SampleBResponse doRequest(RestClientExecutor restClientExecutor, SampleBRequest requestDto) throws EndpointOperationException {
        return null;
    }

    @Override
    public String endpointName() {
        return "";
    }

    @Override
    public String serviceName() {
        return "";
    }
}
