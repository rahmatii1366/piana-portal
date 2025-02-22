package ir.piana.boot.endpoint.servicepoint;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public final class PianaRestClient {
    private final RestClient restClient;


}
