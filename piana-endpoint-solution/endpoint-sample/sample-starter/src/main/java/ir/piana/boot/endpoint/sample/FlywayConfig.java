package ir.piana.boot.endpoint.sample;

import ir.piana.boot.endpoint.manager.FlywayMigratable;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
//@RequiredArgsConstructor
public class FlywayConfig {
    /*@Bean
    public FluentConfiguration sampleStarterFluentConfiguration() {
        FluentConfiguration endpoints = Flyway.configure().schemas("endpoints")
                .locations("db/migration/endpoint-dml")
                .baselineOnMigrate(true)
                .baselineVersion("2.0");
        return endpoints;
    }*/
}