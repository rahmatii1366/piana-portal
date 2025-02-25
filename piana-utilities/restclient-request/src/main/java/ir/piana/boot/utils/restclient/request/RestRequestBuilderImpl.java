package ir.piana.boot.utils.restclient.request;

import org.springframework.http.HttpHeaders;

import java.util.LinkedHashMap;
import java.util.Map;

class RestRequestBuilderImpl implements RestRequestBuilder {
    final byte[] body;
    final HttpHeaders headers = new HttpHeaders();
    final Map<String, String> queryParam = new LinkedHashMap<>();
    final Map<String, String> pathParam = new LinkedHashMap<>();

    RestRequestBuilderImpl() {
        this.body = null;
    }

    RestRequestBuilderImpl(byte[] body) {
        this.body = body;
    }

    public RestRequest build() {
        return new RestRequestImpl(body, headers, queryParam, pathParam);
    }
}
