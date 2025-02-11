package ir.piana.dev.openidc.data.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ir.piana.dev.openidc.core.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
@ConfigurationProperties(prefix = "oidc.service.jwt-token")
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    //    private final AuthenticationValidityCacheService authenticationValidityCacheService;
    @Setter
    private String secretKey;
    @Setter
    private Long refreshExpiration = 0l;
    @Setter
    private String issuer;
    @Setter
    private String audience;

    @Override
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public String extractType(String jwtToken) {
        return extractClaim(jwtToken, claims -> claims.get("type", String.class));
    }

    @Override
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(String username, long expiration) {
        return generateToken(new HashMap<>(), username, expiration);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, String username, long expiration) {
        extraClaims.put("type", Type.ACCESS.name());
        return buildToken(extraClaims, username, expiration);
    }

    @Override
    public String generateRefreshToken(String username) {
        return buildToken(Map.of("type", Type.REFRESH.name()), username, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuer(issuer)
                .setAudience(audience)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwtToken, String username) {
        /*if (!authenticationValidityCacheService.checkValidity(jwtToken))
            return false;*/
        final String tokenUsername = extractUsername(jwtToken);
        return tokenUsername.equals(username) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpirationDate(jwtToken).before(new Date());
    }

    private Date extractExpirationDate(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }


    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Optional<String> extractJwtToken(String authHeader) {
        String jwtToken = null;
        if (isValidBearerAuthToken(authHeader)) {
            jwtToken = getJwtTokenFromAuthHeader(authHeader);
        }
        return Optional.ofNullable(jwtToken);
    }

    public boolean isValidBearerAuthToken(String authHeader) {
        return Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ");
    }

    public String getJwtTokenFromAuthHeader(String authHeader) {
        return authHeader.substring(7);
    }
}
