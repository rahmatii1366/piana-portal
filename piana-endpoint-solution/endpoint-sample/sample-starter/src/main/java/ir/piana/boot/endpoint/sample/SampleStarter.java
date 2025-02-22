package ir.piana.boot.endpoint.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ir.piana")
public class SampleStarter {
    public static void main(String[] args) {
        SpringApplication.run(SampleStarter.class, args);
    }
}
