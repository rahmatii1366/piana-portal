package ir.piana.boot.endpoint.manager.dto;

import java.sql.Timestamp;

public record MerchantInfo(
        long id,
        String name,
        String description,
        boolean disabled,
        Timestamp create_on) {
}
