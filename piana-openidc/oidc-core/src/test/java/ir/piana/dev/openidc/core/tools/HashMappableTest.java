package ir.piana.dev.openidc.core.tools;

import ir.piana.dev.openidc.core.service.auth.redisobj.TokenRelatedModel;
import ir.piana.dev.openidc.core.service.auth.redisobj.UserEntranceModel;
import ir.piana.boot.utils.jedisutils.JedisPool;
import ir.piana.boot.utils.jedisutils.JedisProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author m.rahmati on 1/2/2025
 */
public class HashMappableTest {
    private static JedisPool jedisPool;

    @BeforeEach
    public void setUp() {
        Pool<Jedis> jedisPool = JedisProvider.getJedisPool("/jedis.properties");
        HashMappableTest.jedisPool = new JedisPool(jedisPool, "piana.oidc.test.", 20 * 60);
    }

    @Test
    void testUserEntranceModel() {
        UserEntranceModel dto = new UserEntranceModel(
                "mj.rahmati", 0, false, 5, 1
        );
        jedisPool.setRedisHashMappable(dto);
        UserEntranceModel redisHashMappable = jedisPool.getRedisHashMappable(
                UserEntranceModel.class, "mj.rahmati");
        Assertions.assertEquals(redisHashMappable, dto);
    }

    @Test
    void testTokenRelatedModel() {
        String token = UUID.randomUUID().toString();
        TokenRelatedModel dto = new TokenRelatedModel(
                token, "mj.rahmati", 1, "WEB",
                30, "localhost",
                System.currentTimeMillis(), new ArrayList<>());

        jedisPool.setRedisHashMappable(dto);
        TokenRelatedModel redisHashMappable = jedisPool.getRedisHashMappable(
                TokenRelatedModel.class, token);
        Assertions.assertEquals(redisHashMappable, dto);
    }

    @Test
    void testDeleteAll() {
        for (int i = 0; i < 1000; i++) {
            UserEntranceModel dto = new UserEntranceModel(
                    "mj.rahmati" + i, 0, false, 5, 1
            );
            jedisPool.setRedisHashMappable(dto);
        }
        jedisPool.removeAll();
    }
}
