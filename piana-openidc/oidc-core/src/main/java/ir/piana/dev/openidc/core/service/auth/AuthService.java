package ir.piana.dev.openidc.core.service.auth;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.service.auth.dto.AuthDto;
import ir.piana.dev.openidc.core.service.user.dto.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthService {
    UserDetails byUsernameAndToken(String username, String token);
    ApiResponse<AuthDto> loginWithPasswordCredential(
            String username, String password,
            String connectivityChannel, long domainId, String ip);
    ApiResponse<?> changePassword(String password, String newPassword);
    ApiResponse<AuthDto> refresh(String token);
    ApiResponse<Boolean> hasPermission(String perm);
}
