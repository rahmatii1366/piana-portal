package ir.piana.boot.endpoint.provider.one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ir.piana")
public class OneStarter {
    public static void main(String[] args) {
        SpringApplication.run(OneStarter.class, args);
    }
}
