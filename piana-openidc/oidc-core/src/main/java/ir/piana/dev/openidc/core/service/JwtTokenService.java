package ir.piana.dev.openidc.core.service;

import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface JwtTokenService {
    String extractUsername(String jwtToken);

    String extractType(String jwtToken);

    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);

    String generateToken(String username, long expiration);

    String generateToken(Map<String, Object> extraClaims, String username, long expiration);

    String generateRefreshToken(String username);

    boolean isTokenValid(String jwtToken, String username);

    Optional<String> extractJwtToken(String authHeader);

    boolean isValidBearerAuthToken(String authHeader);

    String getJwtTokenFromAuthHeader(String authHeader);

    /**
     * Token types.
     */
    enum Type {
        ACCESS,
        REFRESH,
    }
}
