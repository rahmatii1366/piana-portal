package ir.piana.boot.inquiry.endpoint.manager;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
public class FlywayConfig implements FlywayMigrationStrategy {
    @Override
    public void migrate(Flyway flyway) {
        var datasource = flyway.getConfiguration().getDataSource();

        Flyway endpoint = Flyway.configure()
                .schemas("endpoint_schema")
                .locations("db/migration/endpoint")
                .dataSource(datasource).load();

        endpoint.migrate();
    }
}