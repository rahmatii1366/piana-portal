package ir.piana.boot.endpoint.sample.operation.providerone;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.boot.endpoint.operation.EndpointOperationException;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoServiceOperation;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoRequest;
import ir.piana.boot.endpoint.sample.service.vehicleinfo.VehicleInfoResponse;
import ir.piana.boot.utils.restclient.request.RestClientExecutor;
import ir.piana.boot.utils.restclient.request.RestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class VehicleInfoOperationProviderOne extends VehicleInfoServiceOperation {
    private final ObjectMapper objectMapper;

    @Override
    public VehicleInfoResponse doRequest(RestClientExecutor restClientExecutor, VehicleInfoRequest requestDto)
            throws EndpointOperationException {
        RestClient.ResponseSpec request = restClientExecutor.request(RestRequest.builder()
                .bodyLess().then()
                .setQueryParam("vin", requestDto.vin())
                .setQueryParam("productionYear", String.valueOf(requestDto.productionYear()))
                .then().noPathParam()
                .build());

        return request.body(VehicleInfoResponse.class);
        /*return restClient.get().uri("api/v1/provider/one/inquiry?" +
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
                });*/
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
