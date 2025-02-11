package ir.piana.dev.utils.jedisutils.hashmappable;

import ir.piana.dev.utils.jedisutils.JedisPool;
import ir.piana.dev.utils.jedisutils.RedisHashMappable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author m.rahmati on 1/2/2025
 */
public class TokenRelatedModel implements RedisHashMappable {
    private String token;
    private int domainId;
    private String ip;
    private long lastAccessTime;
    private UserInfo userInfo;

    public TokenRelatedModel() {
        userInfo = new UserInfo();
    }

    public TokenRelatedModel(
            int domainId, String ip, long lastAccessTime,
            UserInfo userInfo) {
        this.domainId = domainId;
        this.ip = ip;
        this.lastAccessTime = lastAccessTime;
        this.userInfo = userInfo;
    }

    public int getDomainId() {
        return domainId;
    }

    public String getIp() {
        return ip;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("domainId", String.valueOf(domainId));
            put("ip", ip);
            put("lastAccessTime", String.valueOf(lastAccessTime));
            putAll(userInfo.toRedisHashMap());
        }};
    }

    public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
        TokenRelatedModel other = fromRedisHashMap(redisHashMap);
        this.userInfo = other.userInfo;
        this.domainId = other.domainId;
        this.ip = other.ip;
        this.lastAccessTime = other.lastAccessTime;
    }

    public List<String> redisKeys() {
        return Stream.of(Arrays.asList(
                                "domainId",
                                "ip",
                                "lastAccessTime"),
                        userInfo.redisKeys())
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    public String redisHashKey(String... hashKeys) {
            if (hashKeys != null && hashKeys.length > 0) {
                return "tokenRelated." + String.join(".", hashKeys);
            }
            return "tokenRelated." + userInfo.getToken();
    }

    public static TokenRelatedModel fromRedisHashMap(Map<String, String> hashMap) {
        return new TokenRelatedModel(
                Integer.parseInt(hashMap.get("domainId")),
                hashMap.get("ip"),
                Long.parseLong(hashMap.get("lastAccessTime")),
                UserInfo.fromRedisHashMap(hashMap));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TokenRelatedModel))
            return false;
        TokenRelatedModel other = (TokenRelatedModel) obj;
        return other.domainId == this.domainId &&
                other.ip.equals(this.ip) &&
                other.lastAccessTime == this.lastAccessTime &&
                other.userInfo.equals(this.userInfo);
    }

    public void refresh(JedisPool jedisPool) {
        this.lastAccessTime = System.currentTimeMillis();
        jedisPool.flushField(this, "lastAccessTime");
    }

    public void reset(
            long lastAccessTime,
            String token) {
        this.lastAccessTime = lastAccessTime;
        this.userInfo.setToken(token);
    }
}
