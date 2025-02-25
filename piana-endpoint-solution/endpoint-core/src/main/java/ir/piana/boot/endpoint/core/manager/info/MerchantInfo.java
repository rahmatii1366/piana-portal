package ir.piana.boot.endpoint.core.manager.info;

import java.sql.Timestamp;

public record MerchantInfo(
        long id,
        String name,
        String description,
        boolean disabled,
        Timestamp create_on) {
}
