package ir.piana.boot.inquiry.core.httpclient.consumer;

import ir.piana.boot.inquiry.common.httpclient.HttpClientProperties;
import ir.piana.boot.inquiry.common.nats.MessageHandler;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.List;

@Component
public class EndpointRefreshNatsSubscriber implements MessageHandler<EndpointRefreshNatsSubscriber.Request> {
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
        return Request.class;
    }

    @Override
    public Object apply(Request request) {
        endpointRestClientBeanCreatorConfig.refresh(request.httpClientProperties);
        return null;
    }

    record Request (List<HttpClientProperties> httpClientProperties) {}
}
