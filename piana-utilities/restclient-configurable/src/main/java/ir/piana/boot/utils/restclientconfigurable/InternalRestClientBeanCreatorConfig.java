package ir.piana.boot.utils.restclientconfigurable;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "piana.tools.internal-client.enabled", havingValue = "true", matchIfMissing = false)
public class InternalRestClientBeanCreatorConfig {
    private static Logger logger = LoggerFactory.getLogger(InternalRestClientBeanCreatorConfig.class);

    private final Clients clients;
    private final RestClientBuilderUtils restClientBuilderUtils;

    @ConfigurationProperties(prefix = "piana.tools.internal-client")
    public record Clients(
            List<HttpEndpointDto> clients
    ) {
    }

    @ConstructorBinding
    public InternalRestClientBeanCreatorConfig(
            Clients clients,
            RestClientBuilderUtils restClientBuilderUtils) {
        this.clients = clients;
        this.restClientBuilderUtils = restClientBuilderUtils;
    }

    @PostConstruct
    public void onPostConstruct() {
        clients.clients.forEach(restClientBuilderUtils::registerRestClient);
    }
}
