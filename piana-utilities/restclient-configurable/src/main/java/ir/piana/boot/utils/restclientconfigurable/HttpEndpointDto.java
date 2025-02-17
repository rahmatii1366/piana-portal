package ir.piana.boot.utils.restclientconfigurable;

import ir.piana.boot.utils.jedisutils.RedisHashMappable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HttpEndpointDto implements RedisHashMappable {
    private long id;
    private String name;
    private boolean isDebugMode;
    private boolean isSecure;
    private String host;
    private int port;
    private String baseUrl;
    private Long soTimeout;
    private Long connectionTimeout;
    private Long socketTimeout;
    private Long timeToLive;
    private String poolReusePolicy; // LIFO; FIFO
    private String poolConcurrencyPolicy;
    private String trustStore;
    private String trustStorePassword;
    private long updateOn;
    private List<String> tlsVersions;

    @Override
    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("id", String.valueOf(id));
            put("name", name);
            put("isDebugMode", String.valueOf(isDebugMode));
            put("isSecure", String.valueOf(isSecure));
            put("host", host);
            put("port", String.valueOf(port));
            put("baseUrl", baseUrl);
            put("soTimeout", String.valueOf(soTimeout));
            put("connectionTimeout", String.valueOf(connectionTimeout));
            put("socketTimeout", String.valueOf(socketTimeout));
            put("timeToLive", String.valueOf(timeToLive));
            put("poolReusePolicy", poolReusePolicy); // LIFO, FIFO
            put("poolConcurrencyPolicy", poolConcurrencyPolicy);
            put("trustStore", trustStore);
            put("trustStorePassword", trustStorePassword);
            put("updateOn", String.valueOf(updateOn));
            put("tlsVersions", String.join(",", tlsVersions));
        }};
    }

    @Override
    public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
        this.id = Long.parseLong(redisHashMap.get("id"));
        this.name = redisHashMap.get("name");
        this.isDebugMode = Boolean.parseBoolean(redisHashMap.get("isDebugMode"));
        this.isSecure = Boolean.parseBoolean(redisHashMap.get("isSecure"));
        this.host = redisHashMap.get("host");
        this.port = Integer.parseInt(redisHashMap.get("port"));
        this.baseUrl = redisHashMap.get("baseUrl");
        this.soTimeout = Long.parseLong(redisHashMap.get("soTimeout"));
        this.connectionTimeout = Long.parseLong(redisHashMap.get("connectionTimeout"));
        this.socketTimeout = Long.parseLong(redisHashMap.get("socketTimeout"));
        this.timeToLive = Long.parseLong(redisHashMap.get("timeToLive"));
        this.poolReusePolicy = redisHashMap.get("poolReusePolicy"); // LIFO, FIFO
        this.poolConcurrencyPolicy = redisHashMap.get("poolConcurrencyPolicy");
        this.trustStore = redisHashMap.get("trustStore");
        this.trustStorePassword = redisHashMap.get("trustStorePassword");
        this.updateOn = Integer.parseInt(redisHashMap.get("updateOn"));
        this.tlsVersions = Arrays.stream(redisHashMap.get("tlsVersions").split(",")).toList();
    }

    @Override
    public List<String> redisKeys() {
        return List.of("id",
                "name",
                "isDebugMode",
                "isSecure",
                "host",
                "port", "baseUrl",
                "soTimeout",
                "connectionTimeout",
                "socketTimeout",
                "timeToLive",
                "poolReusePolicy",
                "poolConcurrencyPolicy",
                "trustStore",
                "trustStorePassword",
                "updateOn",
                "tlsVersions");
    }

    @Override
    public String redisHashKey(String... hashKeys) {
        if (hashKeys != null && hashKeys.length > 0) {
            return "endpoints." + String.join(".", hashKeys);
        }
        return "endpoints." + id;
    }
}
