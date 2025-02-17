package ir.piana.boot.utils.restclientconfigurable;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Endpoints {
    private List<HttpEndpointDto> httpClientProperties;
}
