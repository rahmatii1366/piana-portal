package ir.piana.dev.openidc.gateway.controller.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
    @NotNull
    private String channel;
    @NotNull
    private Long domainId;
}
