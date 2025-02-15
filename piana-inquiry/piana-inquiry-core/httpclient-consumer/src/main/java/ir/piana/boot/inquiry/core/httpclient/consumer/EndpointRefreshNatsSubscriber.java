package ir.piana.boot.inquiry.core.httpclient.consumer;

import ir.piana.boot.inquiry.common.httpclient.Endpoints;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;
import ir.piana.boot.utils.natsclient.MessageHandler;

@Component
public class EndpointRefreshNatsSubscriber implements MessageHandler<Endpoints> {
    private final ResourceLoader resourceLoader;
    private final GenericWebApplicationContext applicationContext;
    private final EndpointRestClientBeanCreatorConfig endpointRestClientBeanCreatorConfig;

    public EndpointRefreshNatsSubscriber(
            ResourceLoader resourceLoader,
            GenericWebApplicationContext applicationContext,
            EndpointRestClientBeanCreatorConfig endpointRestClientBeanCreatorConfig) {
        this.resourceLoader = resourceLoader;
        this.applicationContext = applicationContext;
        this.endpointRestClientBeanCreatorConfig = endpointRestClientBeanCreatorConfig;
    }

    @Override
    public String subject() {
        return "piana.inquiry.endpoint.refreshed";
    }

    @Override
    public Class dtoType() {
        return Endpoints.class;
    }

    @Override
    public Object apply(Endpoints endpoints) {
        endpointRestClientBeanCreatorConfig.refresh(endpoints.getHttpClientProperties());
        return null;
    }
}
