package ir.piana.dev.openidc.core.service.user.dto;

import ir.piana.dev.openidc.core.filter.SimpleGrantedAuthority;

import java.util.Set;

public record UserDetails(
        String username,
        Long id,
        String token,
        Set<SimpleGrantedAuthority> authorities) {
    public UserDetails(String username, String token) {
        this(username, null, token, null);
    }
}
