package ir.piana.dev.openidc.core.service.auth.redisobj;

import ir.piana.boot.utils.jedisutils.JedisPool;
import ir.piana.boot.utils.jedisutils.RedisHashMappable;

import java.util.*;

public class UserEntranceModel implements RedisHashMappable {
    private String username;
    private int failedTryCount;
    private boolean failedTryCountUpstream;
    private int volume;
    private double rate;
    private long lastAccess;
    private double currentVolume;
    private List<String> tokens;

    public UserEntranceModel() {
        this(null, 0, false, 0, 0, 0, 0, new ArrayList<>());
    }

    public UserEntranceModel(String username, int failedTryCount, boolean failedTryCountUpstream, int volume, double rate) {
        this(username, failedTryCount, failedTryCountUpstream, volume, rate / 1000, volume, System.currentTimeMillis(),
                new ArrayList<>());
    }

    public UserEntranceModel(String username, int failedTryCount, boolean failedTryCountUpstream, int volume, double rate, long lastAccess, double currentVolume) {
        this(username, failedTryCount, failedTryCountUpstream, volume, rate, lastAccess, currentVolume, new ArrayList<>());
    }

    public UserEntranceModel(String username, int failedTryCount, boolean failedTryCountUpstream, int volume, double rate, long lastAccess, double currentVolume, List<String> tokens) {
        this.username = username;
        this.failedTryCount = failedTryCount;
        this.failedTryCountUpstream = failedTryCountUpstream;
        this.volume = volume;
        this.rate = rate;
        this.lastAccess = lastAccess;
        this.currentVolume = currentVolume;
        this.tokens = tokens == null ? new ArrayList<>() : tokens;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(JedisPool jedisPool, String username) {
        this.username = username;
        jedisPool.flushField(this, "username");
    }

    public int getFailedTryCount() {
        return failedTryCount;
    }

    public void incrementFailedTryCount(JedisPool jedisPool) {
        this.failedTryCount++;
        jedisPool.flushField(this, "failedTryCount");
    }

    public boolean isFailedTryCountUpstream() {
        return failedTryCountUpstream;
    }

    public void sendFailedTryCountUpstream(JedisPool jedisPool) {
        this.failedTryCountUpstream = true;
        jedisPool.flushField(this, "failedTryCountUpstream");
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(JedisPool jedisPool, int volume) {
        this.volume = volume;
        jedisPool.flushField(this, "volume");
    }

    public double getRate() {
        return rate;
    }

    public void setRate(JedisPool jedisPool, double rate) {
        this.rate = rate;
        jedisPool.flushField(this, "rate");
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(JedisPool jedisPool, long lastAccess) {
        this.lastAccess = lastAccess;
        jedisPool.flushField(this, "lastAccess");
    }

    public double getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(JedisPool jedisPool, double currentVolume) {
        this.currentVolume = currentVolume;
        jedisPool.flushField(this, "currentVolume");
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(JedisPool jedisPool, List<String> tokens) {
        this.tokens = tokens;
        jedisPool.flushField(this, "token");
    }

    public void addToken(JedisPool jedisPool, String token) {
        this.tokens.add(token);
        jedisPool.flushField(this, "tokens");
    }

    public void removeToken(JedisPool jedisPool, String token) {
        this.tokens.remove(token);
        jedisPool.flushField(this, "tokens");
    }

    public synchronized boolean entrance(JedisPool jedisPool, double weight) {
        if (this.volume == 0 || weight <= 0)
            return true;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < lastAccess) {
            lastAccess = currentTimeMillis;
            currentVolume = volume;

            jedisPool.flushField(this, "lastAccess", "currentVolume");
            return true;
        }
        currentVolume += (currentTimeMillis - lastAccess) * rate;
        if (currentVolume > volume)
            currentVolume = volume;
        if (currentVolume < 0)
            currentVolume = 0;
        lastAccess = currentTimeMillis;
        if (currentVolume >= weight) {
            currentVolume -= weight;

            jedisPool.flushField(this, "lastAccess", "currentVolume");
            return true;
        }
        jedisPool.flushField(this, "lastAccess", "currentVolume");
        return false;
    }

    @Override
    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("username", username);
            put("failedTryCount", String.valueOf(failedTryCount));
            put("failedTryCountUpstream", String.valueOf(failedTryCountUpstream));
            put("volume", String.valueOf(volume));
            put("rate", String.valueOf(rate));
            put("lastAccess", String.valueOf(lastAccess));
            put("currentVolume", String.valueOf(currentVolume));
            put("tokens", tokens == null ? "" : String.join(",", tokens));
        }};
    }

    public static UserEntranceModel fromRedisHashMap(Map<String, String> hashMap) {
        final String theTokens = hashMap.get("tokens") == null ? "" : hashMap.get("tokens");
        return new UserEntranceModel(
                hashMap.get("username"),
                Integer.parseInt(Optional.ofNullable(hashMap.get("failedTryCount")).orElse("0")),
                Boolean.parseBoolean(Optional.ofNullable(hashMap.get("failedTryCountUpstream")).orElse("false")),
                Integer.parseInt(hashMap.get("volume")),
                Double.parseDouble(hashMap.get("rate")),
                Long.parseLong(hashMap.get("lastAccess")),
                Double.parseDouble(hashMap.get("currentVolume")),
                (theTokens == null || theTokens.isEmpty()) ? new ArrayList<>() :
                        new ArrayList<>() {{
                            addAll(Arrays.asList(theTokens.split(",")));
                        }}
        );
    }

    @Override
    public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
        UserEntranceModel other = fromRedisHashMap(redisHashMap);
        username = other.username;
        failedTryCount = other.failedTryCount;
        failedTryCountUpstream = other.failedTryCountUpstream;
        tokens = other.tokens;
        volume = other.volume;
        rate = other.rate;
        lastAccess = other.lastAccess;
        currentVolume = other.currentVolume;
    }

    @Override
    public List<String> redisKeys() {
        return List.of("username", "failedTryCount", "failedTryCountUpstream", "volume", "rate", "lastAccess", "currentVolume", "tokens");
    }

    @Override
    public String redisHashKey(String... hashKeys) {
        if (hashKeys != null && hashKeys.length > 0) {
            return "userEntrance." + String.join(".", hashKeys);
        }
        return "userEntrance." + username;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserEntranceModel other))
            return false;
        return other.username.equals(this.username) &&
                other.failedTryCount == this.failedTryCount &&
                other.failedTryCountUpstream == this.failedTryCountUpstream &&
                other.volume == this.volume &&
                other.rate == this.rate &&
                other.lastAccess == this.lastAccess &&
                other.currentVolume == this.currentVolume &&
                other.tokens.equals(this.tokens);
    }
}
