package ir.piana.boot.endpoint.provider.two;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ir.piana")
public class TwoStarter {
    public static void main(String[] args) {
        SpringApplication.run(TwoStarter.class, args);
    }
}
