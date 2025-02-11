package ir.piana.dev.openidc.core.service.auth.redisobj;

import ir.piana.dev.utils.jedisutils.JedisPool;
import ir.piana.dev.utils.jedisutils.RedisHashMappable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TokenRelatedModel implements RedisHashMappable {
    private String token;
    private String username;
    private long userId;
    private String channel;
    private long domainId;
    private String ip;
    private long lastAccessTime;
    private List<Long> uiPermissions;

    public TokenRelatedModel() {
        this(null, null, 0, null, 0, null, 0, new ArrayList<>());
    }

    public TokenRelatedModel(
            String token, String username, long userId,
            String channel, long domainId, String ip, long lastAccessTime,
            List<Long> uiPermissions) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.channel = channel;
        this.domainId = domainId;
        this.ip = ip;
        this.lastAccessTime = lastAccessTime;
        this.uiPermissions = uiPermissions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(JedisPool jedisPool, String token) {
        this.token = token;
        jedisPool.flushField(this, "token");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(JedisPool jedisPool, String username) {
        this.username = username;
        jedisPool.flushField(this, "username");
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(JedisPool jedisPool, long userId) {
        this.userId = userId;
        jedisPool.flushField(this, "userId");
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(JedisPool jedisPool, String channel) {
        this.channel = channel;
        jedisPool.flushField(this, "channel");
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(JedisPool jedisPool, long domainId) {
        this.domainId = domainId;
        jedisPool.flushField(this, "domainId");
    }

    public String getIp() {
        return ip;
    }

    public void setIp(JedisPool jedisPool, String ip) {
        this.ip = ip;
        jedisPool.flushField(this, "ip");
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(JedisPool jedisPool, long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        jedisPool.flushField(this, "lastAccessTime");
    }

    public List<Long> getUiPermissions() {
        return uiPermissions;
    }

    public void setUiPermissions(JedisPool jedisPool, List<Long> uiPermissions) {
        this.uiPermissions = uiPermissions;
        jedisPool.flushField(this, "uiPermissions");
    }

    @Override
    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("token", String.valueOf(token));
            put("username", username);
            put("userId", String.valueOf(userId));
            put("channel", String.valueOf(channel));
            put("domainId", String.valueOf(domainId));
            put("ip", ip);
            put("lastAccessTime", String.valueOf(lastAccessTime));
            put("uiPermissions", uiPermissions.stream()
                    .map(String::valueOf).collect(Collectors.joining(",")));
        }};
    }

    public static TokenRelatedModel fromRedisHashMap(Map<String, String> hashMap) {
        String theUiPermissions = hashMap.get("uiPermissions");
        return new TokenRelatedModel(
                hashMap.get("token"),
                hashMap.get("username"),
                Long.parseLong(hashMap.get("userId")),
                hashMap.get("channel"),
                Integer.parseInt(hashMap.get("domainId")),
                hashMap.get("ip"),
                Long.parseLong(hashMap.get("lastAccessTime")),
                theUiPermissions == null || theUiPermissions.isEmpty() ?
                        new ArrayList<>() :
                        Arrays.stream(theUiPermissions.split(",")).map(Long::parseLong).toList()
        );
    }

    @Override
    public void reloadFromRedisHashMap(Map<String, String> map) {
        TokenRelatedModel tokenRelatedModel = fromRedisHashMap(map);
        this.token = tokenRelatedModel.token;
        this.username = tokenRelatedModel.username;
        this.userId = tokenRelatedModel.userId;
        this.channel = tokenRelatedModel.channel;
        this.domainId = tokenRelatedModel.domainId;
        this.ip = tokenRelatedModel.ip;
        this.lastAccessTime = tokenRelatedModel.lastAccessTime;
        this.uiPermissions = tokenRelatedModel.uiPermissions;
    }

    @Override
    public List<String> redisKeys() {
        return Stream.of(Arrays.asList(
                        "token",
                        "username",
                        "userId",
                        "channel",
                        "domainId",
                        "ip",
                        "lastAccessTime",
                        "uiPermissions"))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public String redisHashKey(String... hashKeys) {
        if (hashKeys != null && hashKeys.length > 0) {
            return "tokenRelated." + String.join(".", hashKeys);
        }
        return "tokenRelated." + token;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TokenRelatedModel other))
            return false;
        return other.token.equals(this.token) &&
                other.username.equals(this.username) &&
                other.userId == this.userId &&
                other.channel.equals(this.channel) &&
                other.domainId == this.domainId &&
                other.ip.equals(this.ip) &&
                other.lastAccessTime == this.lastAccessTime &&
                other.uiPermissions.equals(this.uiPermissions);
    }
}
