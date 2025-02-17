package ir.piana.dev.inquiry.publisher;

import ir.piana.boot.utils.restclientconfigurable.InternalRestClientBeanCreatorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "ir.piana.boot", "ir.piana.dev" })
@EnableConfigurationProperties(value = {
		InternalRestClientBeanCreatorConfig.Clients.class
})
public class PianaInquiryPublisherApplication {

	public static void main(String[] args) {
		SpringApplication.run(PianaInquiryPublisherApplication.class, args);
	}


}
