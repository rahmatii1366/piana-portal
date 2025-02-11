package ir.piana.dev.openidc.gateway.controller.auth.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String password;
    private String newPassword;
}
