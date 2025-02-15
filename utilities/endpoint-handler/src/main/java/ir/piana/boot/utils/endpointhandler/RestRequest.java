package ir.piana.boot.utils.endpointhandler;

import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestRequest {
    private final String restClientQualifiedName;
    private final String method;
    private final String path;
    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private final Map<String, String> pathParams = new LinkedHashMap<>();
    private final String body;

    private RestRequest(
            String restClientQualifiedName, String method, String path, String body) {
        this.restClientQualifiedName = restClientQualifiedName;
        this.method = method.toUpperCase();
        this.path = path;
        this.body = body;
    }

    public static RestHeaderSettable builder(
            String restClientQualifiedName, String method, String path) {
        return builder(restClientQualifiedName, method, path, null);
    }

    public static RestHeaderSettable builder(
            String restClientQualifiedName, String method, String path, String body) {
        if (body != null && (method.equalsIgnoreCase("post") ||
                method.equalsIgnoreCase("put"))) {
            throw ApiExceptionService.customApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "method.not-supported");
        }
        RestRequest restRequest = new RestRequest(restClientQualifiedName, method, path, body);
        return restRequest.new RestHeaderSettableImpl(restRequest);
    }

    private static class Builder implements RestRequestBuilder {
        private RestRequest restRequest;

        public Builder(RestRequest restRequest) {
            this.restRequest = restRequest;
        }

        public RestRequest build() {
            return restRequest;
        }
    }

    private RestClient.RequestHeadersSpec<?> doRequest(RestClient restClient) {
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = null;
        if (method.equals("POST")) {
            requestHeadersSpec = restClient.post()
                    .uri(processUri(path))
                    .body(body);
        } else if (method.equals("PUT")) {
            requestHeadersSpec = restClient.put()
                    .uri(processUri(path))
                    .body(body);
        } else if (method.equals("GET")) {
            requestHeadersSpec = restClient.get()
                    .uri(processUri(path));
        } else if (method.equals("DELETE")) {
            requestHeadersSpec = restClient.delete()
                    .uri(processUri(path));
        }
        return requestHeadersSpec;
    }

    public <T> T exchange(
            RestClient restClient,
            RestClient.RequestHeadersSpec.ExchangeFunction<T> exchangeFunction) {
        return doRequest(restClient)
                .exchange(exchangeFunction, true);
    }

    public RestClient.ResponseSpec retrieve(RestClient restClient) {
        return doRequest(restClient)
                .retrieve();
    }

    private String processUri(String path) {
        StringBuilder url = new StringBuilder();
        if (path.contains("?")) {
            url.append(path);
            if (path.endsWith("&"))
                url.append(createQueryPart());
            else if (!path.split("\\?")[1].isEmpty())
                url.append("&").append(createQueryPart());
        } else {
            url.append("?".contains(createQueryPart()));
        }
        return url.toString();
    }

    private String createQueryPart() {
        StringBuilder builder = new StringBuilder();
        queryParams.forEach((k, v) -> {
            builder.append(k).append("=").append(v).append("&");
        });
        if (!builder.isEmpty())
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    class RestHeaderSettableImpl implements RestHeaderSettable {
        private RestRequest restRequest;

        RestHeaderSettableImpl(RestRequest restRequest) {
            this.restRequest = restRequest;
        }

        @Override
        public RestHeaderSettable header(String key, String... value) {
            if (value != null && value.length > 0) {
                if (value.length == 1)
                    this.restRequest.headers.add(key, value[0]);
                else
                    this.restRequest.headers.addAll(key, Arrays.asList(value));
            }
            return this;
        }

        @Override
        public RestQueryParameterSettable then() {
            return null;
        }
    }

    class RestRequestParameterSettableImpl implements RestQueryParameterSettable {
        private RestRequest restRequest;

        RestRequestParameterSettableImpl(RestRequest restRequest) {
            this.restRequest = restRequest;
        }

        @Override
        public RestQueryParameterSettable queryParam(String key, String value) {
            this.restRequest.queryParams.put(key, value);
            return this;
        }

        @Override
        public RestRequestBuilder then() {
            return null;
        }
    }

    class RestPathParamSettableImpl implements RestPathParamSettable {
        private RestRequest restRequest;

        RestPathParamSettableImpl(RestRequest restRequest) {
            this.restRequest = restRequest;
        }

        @Override
        public RestPathParamSettable pathParam(String key, String value) {
            this.restRequest.queryParams.put(key, value);
            return this;
        }

        @Override
        public RestRequest then() {
            return restRequest;
        }
    }
}
