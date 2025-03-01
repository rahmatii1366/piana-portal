package ir.piana.boot.endpoint.core.manager.dto;

import ir.piana.boot.utils.jedisutils.JedisPool;
import ir.piana.boot.utils.jedisutils.RedisHashMappable;

public abstract class EndpointClientAuthMappable implements RedisHashMappable {
    protected final long endpointClientId;

    protected EndpointClientAuthMappable(long endpointClientId) {
        this.endpointClientId = endpointClientId;
    }

    @Override
    public final String redisHashKey(String... hashKeys) {
        return "endpoint-client-auth." + endpointClientId;
    }

    public static EndpointClientAuthMappable load(JedisPool jedisPool, long endpointClientId) {
        EndpointClientAuthMappable redisHashMappable = jedisPool.getRedisHashMappable(
                EndpointClientAuthMappable.class, String.valueOf(endpointClientId));
        return redisHashMappable;
    }

    public abstract long getTimestamp();
}
