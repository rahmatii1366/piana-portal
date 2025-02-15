package ir.piana.dev.openidc.data.service;

import ir.piana.dev.openidc.core.service.JwtTokenService;
import ir.piana.dev.openidc.core.service.TokenManagementService;
import ir.piana.dev.openidc.core.service.auth.AuthService;
import ir.piana.dev.openidc.core.service.auth.redisobj.LoginIpsModel;
import ir.piana.dev.openidc.core.service.auth.redisobj.TokenRelatedModel;
import ir.piana.dev.openidc.core.service.auth.redisobj.UserEntranceModel;
import ir.piana.dev.openidc.core.service.user.dto.UserDetails;
import ir.piana.boot.utils.jedisutils.JedisPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class TokenManagementServiceImpl implements TokenManagementService {
    private final JwtTokenService jwtService;
    private final AuthService authService;
    private final JedisPool jedisPool;
    //ToDo must be replace by i.e. redis cache
    private static final Set<String> revokedTokens = new LinkedHashSet<>();

    public TokenManagementServiceImpl(
            JwtTokenService jwtService,
            @Lazy AuthService authService,
            JedisPool jedisPool) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.jedisPool = jedisPool;
    }

    public UsernamePasswordAuthenticationToken checkTokenAndReturnUserDetails(String token) {
        TokenRelatedModel tokenRelatedModel = jedisPool.getRedisHashMappable(TokenRelatedModel.class, token);
        if (tokenRelatedModel == null) {
            return null;
        }

        // Extract UserName from JWT token
        String username = tokenRelatedModel.getUsername();

        // Check userName not be null and request has not been authenticated yet
        if (Objects.nonNull(username) && userNotAuthenticatedYet()) {
            UserDetails userDetails = authService.byUsernameAndToken(username, token);
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    username,
                    userDetails.authorities()
            );
            /*if (jwtService.isTokenValid(token, userDetails.username()) && !revokedTokens.contains(token)) {
                return new UsernamePasswordAuthenticationToken(
                        userDetails,
                        username,
                        userDetails.authorities()
                );
            }*/
        }
        return null;
    }

    private boolean userNotAuthenticatedYet() {
        return Objects.isNull(SecurityContextHolder.getContext().getAuthentication());
    }

    public void revokeToken(String token) {
//        revokedTokens.add(jwtToken);
        TokenRelatedModel tokenRelatedModel = jedisPool.removeAndGetRedisHashMappable(
                TokenRelatedModel.class, token);
        UserEntranceModel userEntranceModel = jedisPool.getRedisHashMappable(
                UserEntranceModel.class, tokenRelatedModel.getUsername());
        userEntranceModel.removeToken(jedisPool, tokenRelatedModel.getToken());
        LoginIpsModel loginIpsModel = jedisPool.getRedisHashMappable(
                LoginIpsModel.class,
                tokenRelatedModel.getUsername(),
                tokenRelatedModel.getChannel(),
                String.valueOf(tokenRelatedModel.getDomainId()));
        if (loginIpsModel.getIps().size() > 1)
            loginIpsModel.removeIp(jedisPool, tokenRelatedModel.getIp());
        else
            jedisPool.removeRedisHashMappable(loginIpsModel);
    }
}
