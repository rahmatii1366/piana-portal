package ir.piana.dev.openidc.core.service.auth.redisobj;

import ir.piana.dev.utils.jedisutils.JedisPool;
import ir.piana.dev.utils.jedisutils.RedisHashMappable;

import java.util.*;

public class LoginIpsModel implements RedisHashMappable {
    private String username;
    private String channel;
    private long domainId;
    private List<String> ips;

    public LoginIpsModel() {
        this(null, null, 0, new ArrayList<>());
    }

    public LoginIpsModel(String username, String channel, long domainId, List<String> ips) {
        this.username = username;
        this.channel = channel;
        this.domainId = domainId;
        this.ips = ips;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(JedisPool jedisPool, String username) {
        this.username = username;
        jedisPool.flushField(this, "username");
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

    public List<String> getIps() {
        return ips;
    }

    public void setIps(JedisPool jedisPool, List<String> ips) {
        this.ips = ips;
        jedisPool.flushField(this, "ips");
    }

    public void addIp(JedisPool jedisPool, String ip) {
        this.ips.add(ip);
        jedisPool.flushField(this, "ips");
    }

    public void removeIp(JedisPool jedisPool, String ip) {
        this.ips.remove(ip);
        jedisPool.flushField(this, "ips");
    }

    @Override
    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("username", username);
            put("channel", String.valueOf(channel));
            put("domainId", String.valueOf(domainId));
            put("ips", ips == null ? "" : String.join(",", ips));
        }};
    }

    public static LoginIpsModel fromRedisHashMap(Map<String, String> hashMap) {
        final String theIps = hashMap.get("ips") == null ? "" : hashMap.get("ips");
        return new LoginIpsModel(
                hashMap.get("username"),
                hashMap.get("channel"),
                Long.parseLong(hashMap.get("domainId")),
                (theIps == null || theIps.isEmpty()) ? new ArrayList<>() :
                        new ArrayList<>() {{
                            addAll(Arrays.asList(theIps.split(",")));
                        }}
        );
    }

    @Override
    public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
        LoginIpsModel other = fromRedisHashMap(redisHashMap);
        username = other.username;
        channel = other.channel;
        domainId = other.domainId;
        ips = other.ips;
    }

    @Override
    public List<String> redisKeys() {
        return List.of("username", "channel", "domainId", "ips");
    }

    @Override
    public String redisHashKey(String... hashKeys) {
        if (hashKeys != null && hashKeys.length > 0) {
            return "loginIps." + String.join(".", hashKeys);
        }
        return "loginIps." + username + "." + channel + "." + domainId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LoginIpsModel other))
            return false;
        return other.username.equals(this.username) &&
                other.channel.equals(this.channel) &&
                other.domainId == this.domainId &&
                other.ips.equals(this.ips);
    }
}
