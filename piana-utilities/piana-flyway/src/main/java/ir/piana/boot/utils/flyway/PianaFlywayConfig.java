package ir.piana.boot.utils.flyway;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class PianaFlywayConfig {
    @Bean
    FlywaySure flyway(DataSource dataSource,
                      List<PrimaryFluentConfiguration> primaryMigrations,
                      List<FluentConfiguration> migrations) {
        primaryMigrations.forEach(mig -> mig.fluentConfiguration()
                .dataSource(dataSource)
                .load()
                .migrate());

        migrations.forEach(mig -> mig
                .dataSource(dataSource)
                .load()
                .migrate());
        return new FlywaySure() {
            @Override
            public boolean isMigrate() {
                return true;
            }
        };
    }
}
