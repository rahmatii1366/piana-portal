package ir.piana.dev.openidc;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
public class OidcFlywayConfig implements FlywayMigrationStrategy {
    @Override
    public void migrate(Flyway flyway) {
        var datasource = flyway.getConfiguration().getDataSource();
        Flyway oidc = Flyway.configure()
                .schemas("oidc_schema")
                .locations("db/migration/oidc")
                .dataSource(datasource).load();

        oidc.migrate();
    }
}
