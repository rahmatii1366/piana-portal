package ir.piana.dev.utils.jedisutils.hashmappable;

import ir.piana.dev.utils.jedisutils.JedisPool;
import ir.piana.dev.utils.jedisutils.JedisProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.UUID;

/**
 * @author m.rahmati on 1/2/2025
 */
public class HashMappableTest {
    private static JedisPool jedisPool;

    @BeforeEach
    public void setUp() {
        Pool<Jedis> jedisPool = JedisProvider.getJedisPool("/jedis.properties");
        HashMappableTest.jedisPool = new JedisPool(jedisPool, "mjr.jedis.test.", 20 * 60);
    }

    @Test
    void testTokenRelatedModel() {
        String token = UUID.randomUUID().toString();
        TokenRelatedModel dto = new TokenRelatedModel(
                30, "localhost", System.currentTimeMillis(),
                new UserInfo(token, 10, 81214547,
                        "p.azari", "pooyan", "azari",
                        1, "80000000", "today"));

        jedisPool.setRedisHashMappable(dto);
        TokenRelatedModel redisHashMappable = jedisPool.getRedisHashMappable(
                TokenRelatedModel.class, token);
        Assertions.assertEquals(redisHashMappable, dto);
    }

    @Test
    void testUserInfo() {
        String token = UUID.randomUUID().toString();
        UserInfo dto = new UserInfo(token, 10, 81214547,
                "p.azari", "pooyan", "azari",
                1, "80000000", "today");

        jedisPool.setRedisHashMappable(dto);

        UserInfo redisHashMappable = jedisPool.getRedisHashMappable(
                UserInfo.class, token);
        Assertions.assertEquals(redisHashMappable, dto);
    }

    @Test
    void testDeleteAll() {
        for (int i = 0; i < 1000; i++) {
            UserInfo dto = new UserInfo(UUID.randomUUID().toString(),
                    10, 81214547,
                    "p.azari", "pooyan", "azari",
                    1, "80000000", "today");
            jedisPool.setRedisHashMappable(dto);
        }
        jedisPool.removeAll();
    }
}
