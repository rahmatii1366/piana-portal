package ir.piana.dev.inquiry.publisher.security.authentication.filter;

import ir.piana.boot.utils.errorprocessor.ApiExceptionService;
import ir.piana.boot.utils.errorprocessor.BadRequestTypes;
import ir.piana.dev.inquiry.publisher.security.authentication.AuthenticationType;
import ir.piana.dev.inquiry.publisher.security.authentication.credential.BearerTokenCredential;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class PublisherAuthenticationFilter extends OncePerRequestFilter {
    private final List<AuthenticationProvider> authenticationProviders;

    public PublisherAuthenticationFilter(
            List<AuthenticationProvider> authenticationProviders) {
        this.authenticationProviders = authenticationProviders;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getServletPath().equalsIgnoreCase("/api/v1/auth/login"))
            return true;
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authType = request.getHeader("auth-type");
        if (request.getServletPath().startsWith("/oidc-ui/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<AuthenticationType> authenticationType = AuthenticationType.byName(authType);
        if (authenticationType.isPresent()) {
            Authentication authenticationInfoProvider = switch (authenticationType.get()) {
                case Basic -> {
                    Optional<String> authorization = Optional.ofNullable(
                            request.getHeader("Authorization"));
                    String basicHeader = authorization.orElseThrow(
                            () -> BadRequestTypes.basicHeaderNotSet.newException());
                    if (!basicHeader.startsWith("Basic "))
                        throw BadRequestTypes.basicHeaderNotSet.newException();
                    byte[] decode = Base64.getDecoder().decode(basicHeader.substring(6));
                    String[] basic = new String(decode).split(":");
                    yield new UsernamePasswordAuthenticationToken(basic[0], basic[1]);
                }
                case Bearer -> {
                    Optional<String> authorization = Optional.ofNullable(
                            request.getHeader("Authorization"));
                    String basicHeader = authorization.orElseThrow(
                            () -> ApiExceptionService.customApiException(
                                    HttpStatus.BAD_REQUEST, "bearer-header.not-set"));
                    if (!basicHeader.startsWith("Bearer "))
                        throw ApiExceptionService.customApiException(
                                HttpStatus.BAD_REQUEST, "bearer-header.not-set");
                    yield new BearerTokenCredential(
                            basicHeader.substring(7));
                }
                case BearerJWT -> throw ApiExceptionService.customApiException(
                        HttpStatus.BAD_REQUEST, "authentication-type.not-implemented"
                );
            };

            Authentication authentication = authenticationProviders.stream().filter(
                            authenticationProvider -> authenticationProvider.supports(authenticationInfoProvider.getClass()))
                    .findFirst().orElseThrow(() -> ApiExceptionService.customApiException(
                            HttpStatus.BAD_REQUEST, "authentication-provider.considered.not-founded"
                    ))
                    .authenticate(authenticationInfoProvider);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        /*SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "a", "a", Arrays.asList(new SimpleGrantedAuthority("admin"))));*/
        filterChain.doFilter(request, response);
    }
}
