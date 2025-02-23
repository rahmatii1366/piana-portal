package ir.piana.boot.utils.flyway;

import org.flywaydb.core.api.configuration.FluentConfiguration;

public record PrimaryFluentConfiguration(
        FluentConfiguration fluentConfiguration) {
    /*public String schema() {
        return fluentConfiguration.getSchemas() != null && fluentConfiguration.getSchemas().length > 0 ?
                fluentConfiguration.getSchemas()[0] : "";
    }*/
}
