package ir.piana.dev.openidc.gateway.controller.auth;

import ir.piana.boot.utils.errorprocessor.AuthenticationFailedTypes;
import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.service.TokenManagementService;
import ir.piana.dev.openidc.core.service.auth.AuthService;
import ir.piana.dev.openidc.core.service.auth.dto.AuthDto;
import ir.piana.dev.openidc.core.service.user.dto.UserDetails;
import ir.piana.dev.openidc.gateway.controller.auth.dto.ChangePasswordRequestDto;
import ir.piana.dev.openidc.gateway.controller.auth.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenManagementService tokenManagementService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw AuthenticationFailedTypes.UserNotAuthenticated.newException();
        }
        UserDetails details = ((UserDetails) authentication.getPrincipal());
        tokenManagementService.revokeToken(details.token());
        return ResponseEntity.ok(ApiResponse.builder().build());
    }

    @PostMapping(path = "login")
    public ResponseEntity<ApiResponse<AuthDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ip = request.getRemoteAddr();

        ApiResponse<AuthDto> login = authService.loginWithPasswordCredential(
                loginRequestDto.getUsername(), loginRequestDto.getPassword(),
                loginRequestDto.getChannel(), loginRequestDto.getDomainId(), ip);

        if (login.getCode().equals("success")) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    tokenManagementService.checkTokenAndReturnUserDetails(login.getData().getJwtToken());
            if (authenticationToken != null) {
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        return ResponseEntity.ok(login);
    }

    @PreAuthorize("hasAuthority('PERM_CHANGE_PASSWORD')")
    @PostMapping(path = "change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        ApiResponse<?> login = authService.changePassword(
                changePasswordRequestDto.getPassword(),
                changePasswordRequestDto.getNewPassword());
        return ResponseEntity.ok(login);
    }

    @PreAuthorize("isAuthenticated()")
//    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping(path = "refresh")
    public ResponseEntity<ApiResponse<AuthDto>> refresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApiResponse<AuthDto> refresh = authService.refresh(((UserDetails) authentication.getPrincipal()).token());
        return ResponseEntity.ok(refresh);
    }

    @GetMapping(path = "has-permission")
    public ResponseEntity<ApiResponse<Boolean>> hasPermission(@RequestParam(value = "perm") String perm) {
        return ResponseEntity.ok(authService.hasPermission(perm));
    }
}
