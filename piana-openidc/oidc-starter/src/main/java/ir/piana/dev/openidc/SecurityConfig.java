package ir.piana.dev.openidc;

import ir.piana.dev.openidc.core.filter.OIDCAuthenticationFilter;
import ir.piana.dev.openidc.core.service.TokenManagementService;
import ir.piana.dev.openidc.core.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OIDCAuthenticationFilter oidcAuthenticationFilter(
            AuthService authService,
            TokenManagementService tokenManagementService) {
        return new OIDCAuthenticationFilter(authService, tokenManagementService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, OIDCAuthenticationFilter oidcAuthenticationFilter,
            @Value("${oidc-ui.controller.base-url:oidc-ui}") String prefix) throws Exception {
        prefix = prefix.startsWith("/") ? prefix : "/" + prefix;
        final String urlPrefix = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth/*.requestMatchers("/api/v1/auth/refresh")
                            .authenticated()*/
//                            .requestMatchers("/api/v1/auth/change-password")
//                            .hasAuthority("PERM_CHANGE_PASSWORD")
                            .requestMatchers(
                                    urlPrefix + "/api/v1/auth/login",
                                    urlPrefix + "/api/v1/piana/oidc/domain/all",
                                    urlPrefix + "/api/v1/piana/oidc/permission/all-ui-permissions",
                                    urlPrefix + "/api/v1/auth/refresh",
                                    urlPrefix + "/api/v1/auth/has-permission"
                            ).permitAll()
//                            .requestMatchers(urlPrefix + "/api/v1/piana/oidc/role/all")
//                            .hasAuthority("admin")
                            .anyRequest().authenticated();
                }).sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(oidcAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
