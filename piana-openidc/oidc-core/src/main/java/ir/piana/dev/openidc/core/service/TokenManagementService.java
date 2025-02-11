package ir.piana.dev.openidc.core.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface TokenManagementService {
    UsernamePasswordAuthenticationToken checkTokenAndReturnUserDetails(String jwtToken);
    void revokeToken(String token);
}
