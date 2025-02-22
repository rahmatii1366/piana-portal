package ir.piana.boot.endpoint.operation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class EndpointOperationException extends RuntimeException {
    private HttpStatusCode httpStatusCode;
    private String responseBody;
    private HttpHeaders headers;

    public EndpointOperationException(
            HttpStatusCode httpStatusCode,
            String responseBody,
            HttpHeaders headers) {
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.headers = headers;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (httpStatusCode != null)
            stringBuilder.append("status:").append(httpStatusCode.value()).append(",");
        if (responseBody != null)
            stringBuilder.append(responseBody).append(",");
        return super.toString();
    }
}
