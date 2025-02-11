package ir.piana.dev.openidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.support.CronExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;

@SpringBootApplication(scanBasePackages = {"ir.piana.dev.openidc"})
public class PianaOpenidcApplication {

	public static void main(String[] args) {
		SpringApplication.run(PianaOpenidcApplication.class, args);
	}



//	@Bean
	public ClassLoaderTemplateResolver secondaryTemplateResolver() {
		ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
//		secondaryTemplateResolver.setPrefix("classpath:///templates");
		secondaryTemplateResolver.setPrefix("file:////home/jibit/piana/piana-openidc/oidc-modules/oidc-ui/src/main/resources/templates/");
		secondaryTemplateResolver.setSuffix(".html");
		secondaryTemplateResolver.setCacheable(false);
		secondaryTemplateResolver.setCacheTTLMs(1l);
		secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
		secondaryTemplateResolver.setCharacterEncoding("UTF-8");
		secondaryTemplateResolver.setOrder(1);
		secondaryTemplateResolver.setCheckExistence(true);

		return secondaryTemplateResolver;
	}
}
