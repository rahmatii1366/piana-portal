package ir.piana.boot.utils.restclient.request;

import org.springframework.http.HttpHeaders;

import java.util.Map;

public interface RestRequest {
    byte[] body();
    HttpHeaders headers();
    Map<String, String> queryParam();
    Map<String, String> pathParam();

    static BodySettable builder() {
        return new BodySettableImpl();
    }
}
