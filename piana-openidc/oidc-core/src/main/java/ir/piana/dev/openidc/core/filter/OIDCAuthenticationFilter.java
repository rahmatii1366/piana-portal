package ir.piana.dev.openidc.core.filter;

import ir.piana.dev.openidc.core.service.TokenManagementService;
import ir.piana.dev.openidc.core.service.auth.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class OIDCAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenService jwtService;
    private final AuthService authService;
    private final TokenManagementService tokenManagementService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
//         There is no need to validate
        if (request.getServletPath().equals("/oidc-ui/ui/api/v1/auth/login")) {
            logger.info("auth request");
            filterChain.doFilter(request, response);
            return;
        }

        // Is not a bearer token
        String authHeader = getAuthorizationHeader(request);
        if (!isValidBearerAuthToken(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract jwt token from header
        String bearerToken = getBearerTokenFromAuthHeader(authHeader);

        // Check token type
        /*try {
            if (!JwtTokenService.Type.ACCESS.name().equals(jwtService.extractType(bearerToken))) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException ex) {
            filterChain.doFilter(request, response);
            return;
        }*/

//        if (request.getServletPath().equals("/oidc-ui/ui/api/v1/auth/logout")) {
//            tokenManagementService.revokeToken(bearerToken);
//            response.setStatus(200);
//            return;
//        }

        UsernamePasswordAuthenticationToken authToken = tokenManagementService.checkTokenAndReturnUserDetails(bearerToken);
        if (authToken != null) {
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    public boolean isValidBearerAuthToken(String authHeader) {
        return Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ");
    }

    public String getBearerTokenFromAuthHeader(String authHeader) {
        return authHeader.substring(7);
    }

    private String getAuthorizationHeader(jakarta.servlet.http.HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
