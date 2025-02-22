package ir.piana.boot.endpoint.servicepoint.restop;

import java.net.http.HttpHeaders;

public class RestClientResponse<T> {
    private int httpStatus;
    private String responseBody;
    private T responseDto;
    private HttpHeaders headers;
}
