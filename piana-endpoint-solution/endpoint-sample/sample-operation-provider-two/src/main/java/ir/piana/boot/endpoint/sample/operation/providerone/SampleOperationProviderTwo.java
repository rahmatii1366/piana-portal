package ir.piana.boot.endpoint.sample.operation.providerone;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.boot.endpoint.operation.EndpointOperationException;
import ir.piana.boot.endpoint.sample.service.SampleServiceOperation;
import ir.piana.boot.endpoint.sample.service.VehicleInfoRequest;
import ir.piana.boot.endpoint.sample.service.VehicleInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class SampleOperationProviderTwo extends SampleServiceOperation {
    private final ObjectMapper objectMapper;

    @Override
    public VehicleInfoResponse doRequest(RestClient restClient, VehicleInfoRequest requestDto) throws EndpointOperationException {
        return restClient.get().uri("api/v1/provider/two/inquiry?" +
                        "vin=" + requestDto.vin() + "&productionYear=" + requestDto.productionYear())
                .exchange((request, response) -> {
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        byte[] bytes = response.getBody().readAllBytes();
                        throw new EndpointOperationException(
                                response.getStatusCode(),
                                new String(bytes),
                                response.getHeaders());
                    } else {
                        return objectMapper.readValue(
                                response.getBody(), VehicleInfoResponse.class);
                    }
                });
    }

    @Override
    public String endpointName() {
        return "sample";
    }

    @Override
    public String serviceName() {
        return "inquiry";
    }
}
