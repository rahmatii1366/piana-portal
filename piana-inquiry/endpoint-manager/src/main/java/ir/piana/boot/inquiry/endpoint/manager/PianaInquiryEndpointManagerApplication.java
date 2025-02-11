package ir.piana.boot.inquiry.endpoint.manager;

import ir.piana.boot.inquiry.common.httpclient.InternalRestClientBeanCreatorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"ir.piana.boot", "ir.piana.dev"})
@EnableConfigurationProperties(value = {
        InternalRestClientBeanCreatorConfig.Clients.class
})
public class PianaInquiryEndpointManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PianaInquiryEndpointManagerApplication.class, args);
    }
}
