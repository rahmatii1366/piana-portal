package ir.piana.boot.endpoint.sample.provider.one.authenable;

import ir.piana.boot.endpoint.core.manager.EndpointClientAuthEnable;
import ir.piana.boot.endpoint.core.manager.dto.EndpointClientAuthMappable;
import ir.piana.boot.endpoint.core.manager.info.EndpointClientInfo;
import ir.piana.boot.utils.jedisutils.JedisPool;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ProviderOneClientAuthEnableImpl extends EndpointClientAuthEnable {

    public ProviderOneClientAuthEnableImpl(JedisPool jedisPool, LockingTaskExecutor lockingTaskExecutor) {
        super(jedisPool, lockingTaskExecutor);
    }

    @Override
    protected EndpointClientAuthMappable authenticate(RestClient restClient, EndpointClientInfo endpointClientInfo) {
        AuthResponse authResponse = restClient.post()
                .uri("api/v1/provider/one/authenticate")
                .body(new AuthRequest("clientId", "secretKey"))
                .exchange((clientRequest, clientResponse) -> {
                    if (clientResponse.getStatusCode().is2xxSuccessful())
                        return clientResponse.bodyTo(AuthResponse.class);
                    else throw new RuntimeException();
                });

        return new AuthInfo(endpointClientInfo.id(), authResponse.accessToken, authResponse.refreshToken);
    }

    public record AuthRequest(String clientId, String secretKey) {
    }//1424538700

    public record AuthResponse (String accessToken, String refreshToken) {
    }

    public static class AuthInfo extends EndpointClientAuthMappable {
        private String accessToken;
        private String refreshToken;

        AuthInfo(long endpointClientId, String accessToken, String refreshToken) {
            super(endpointClientId);
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        @Override
        public Map<String, String> toRedisHashMap() {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        @Override
        public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
            this.accessToken = redisHashMap.get("accessToken");
            this.refreshToken = redisHashMap.get("refreshToken");
        }

        @Override
        public List<String> redisKeys() {
            return List.of("accessToken", "refreshToken");
        }
    }
}
