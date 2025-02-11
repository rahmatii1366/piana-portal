package ir.piana.dev.openidc.core.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthDto {
    private String jwtToken;
    private boolean shouldBeChangePassword;
    private String username;
    private List<Long> uiPermissions;
}
