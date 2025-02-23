package ir.piana.boot.endpoint.sample.service.sampleb;

import ir.piana.boot.endpoint.operation.EndpointOperationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SampleBProviderOne extends SampleBServiceOperation {
    @Override
    public SampleBResponse doRequest(RestClient restClient, SampleBRequest requestDto) throws EndpointOperationException {
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
