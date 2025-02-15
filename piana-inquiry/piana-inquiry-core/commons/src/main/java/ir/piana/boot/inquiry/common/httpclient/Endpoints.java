package ir.piana.boot.inquiry.common.httpclient;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Endpoints {
    private List<HttpClientProperties> httpClientProperties;
}
