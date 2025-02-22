package ir.piana.boot.endpoint.manager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "piana.tools.endpoints")
@Getter
@Setter
public class EndpointsProperties {
    private List<String> names;
}
