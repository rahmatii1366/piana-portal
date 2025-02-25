package ir.piana.boot.utils.restclient.request;

import org.springframework.http.HttpHeaders;

import java.util.Map;

record RestRequestImpl(
        byte[] body,
        HttpHeaders headers,
        Map<String, String> queryParam,
        Map<String, String> pathParam) implements RestRequest {
}
