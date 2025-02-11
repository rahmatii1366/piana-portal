package ir.piana.dev.utils.jedisutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.util.Pool;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author m.rahmati on 11/16/2024
 */
public class JedisPool {
    private static Logger log = LoggerFactory.getLogger(JedisPool.class.getName());

    public final int timeOut;// = 20 * 60;
    public final String redisPrefix;
    private final Pool<Jedis> pool;

    public JedisPool(Pool<Jedis> jedisPool, String prefix, int timeOut) {
        this.pool = jedisPool;
        this.timeOut = timeOut <= 0 ? 20 * 60 : timeOut;
        prefix = Optional.ofNullable(prefix).orElse("");

        redisPrefix = (!prefix.isEmpty() && !prefix.endsWith(".")) ?
                prefix.concat(".") : prefix;
    }

    public void removeAll() {
        ScanParams scanParam = new ScanParams();
        scanParam.match(redisPrefix + "*");
        scanParam.count(1000);

        try (Jedis jedis = pool.getResource()) {
            String nextCursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(nextCursor, scanParam);
                List<String> keys = scanResult.getResult();
                nextCursor = scanResult.getCursor();

                jedis.del(keys.toArray(new String[keys.size()]));
            } while (!nextCursor.equals("0"));
        } catch (Exception e) {
            log.error("Pattern: {}, Message: {}", redisPrefix + "*", e.getMessage());
        }
    }

    public Map<String, String> scanKey(String pattern) {
        ScanParams scanParam = new ScanParams();
        scanParam.match(pattern);
        scanParam.count(50000);

        try (Jedis jedis = pool.getResource()) {
            ScanResult<String> value = jedis.scan("0", scanParam);
            List<String> keys = value.getResult();
            List<String> values = jedis.mget(keys.toArray(new String[0]));

            HashMap<String, String> map = new HashMap<>(keys.size());

            for (int i = 0; i < keys.size(); i++)
                map.put(keys.get(i), values.get(i));

            return map;
        } catch (Exception e) {
            log.error("Pattern: {}, Message: {}", pattern, e.getMessage());
        }

        return null;
    }

    public String getKey(String keyName) {
        return getKey(keyName, 0);
    }

    public String getKey(String keyName, int timer) {
        String value = null;

        try (Jedis jedis = pool.getResource()) {
            value = jedis.get(redisPrefix + keyName);
            if (timer != 0)
                jedis.expire(redisPrefix + keyName, timer);
        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }

        return value;
    }

    public String getKey(String hashKey, String keyName) {
        return getKey(hashKey, keyName, 0);
    }

    public String getKey(String hashKey, String keyName, int timer) {
        String value = null;

        try (Jedis jedis = pool.getResource()) {
            value = jedis.hget(redisPrefix + hashKey, keyName);
            if (timer != 0)
                jedis.expire(redisPrefix + hashKey, timer);

        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }

        return value;
    }

    public Map<String, String> getAll(String hashKey) {
        Map<String, String> hashData = null;

        try (Jedis jedis = pool.getResource()) {
            hashData = jedis.hgetAll(redisPrefix + hashKey);
        } catch (Exception e) {
            log.error("HashKey:" + hashKey, "Message:'" + e.getMessage() + "'");
        }
        return hashData;
    }

    public void setKey(String keyName, String keyValue) {
        setKey(keyName, keyValue, 0);
    }

    public void setKey(String keyName, String keyValue, int timer) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(redisPrefix + keyName, keyValue);
            if (timer != 0)
                jedis.expire(redisPrefix + keyName, timer);
        } catch (Exception e) {
            log.error("keyName:" + keyName + " KeyValue:" + keyValue + " Timer:" + timer, "Message:'" + e.getMessage() + "'");
        }
    }

    public void setKey(String hashKey, String keyName, String keyValue) {
        setKey(hashKey, keyName, keyValue, 0);
    }

    public void setKey(String hashKey, String keyName, String keyValue, int timer) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(redisPrefix + hashKey, keyName, keyValue);
            if (timer != 0)
                jedis.expire(redisPrefix + hashKey, timer);
        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }
    }

    public long removeKey(String hashKey, String keyName) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hdel(redisPrefix + hashKey, keyName);
        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }

        return 0;
    }

    public long removeKey(String keyName) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.del(redisPrefix + keyName);
        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }
        return 0;
    }

    public long removeKeys(String... keyName) {
        if (keyName != null && keyName.length > 0) {
            try (Jedis jedis = pool.getResource()) {
                long val = -1;
                for (String k : keyName)
                    val = jedis.del(redisPrefix + keyName);
                return val;
            } catch (Exception e) {
                log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
            }
        }
        return 0;
    }

    public String getAttribute(String key, String keyName) {
        String value = null;
        if (key != null) {
            try (Jedis jedis = pool.getResource()) {
                value = jedis.hget(redisPrefix + key, keyName);
                jedis.expire(redisPrefix + key, timeOut);
            } catch (Exception e) {
                log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
            }
        }

        return value;
    }

    public void setAttribute(String key, String keyName, long keyValue) {
        setAttribute(key, keyName, keyValue + "");
    }

    public void setAttribute(String key, String keyName, int keyValue) {
        setAttribute(key, keyName, keyValue + "");
    }

    public void setAttribute(String key, String keyName, String keyValue) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(redisPrefix + key, keyName, keyValue);
            jedis.expire(redisPrefix + key, timeOut);
        } catch (Exception e) {
            log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
        }
    }

    public List<String> getMultiAttribute(String key, String... keyName) {
        List<String> value = null;

        if (key != null) {
            try (Jedis jedis = pool.getResource()) {
                value = jedis.hmget(redisPrefix + key, keyName);
                jedis.expire(redisPrefix + key, timeOut);
            } catch (Exception e) {
                log.error("keyName:" + keyName, "Message:'" + e.getMessage() + "'");
            }
        }

        return value;
    }

    public void setMultiAttribute(String key, Map<String, String> keyvalues) {
        if (key != null) {
            try (Jedis jedis = pool.getResource()) {
                jedis.hmset(redisPrefix + key, keyvalues);
                jedis.expire(redisPrefix + key, timeOut);
            } catch (Exception e) {
                log.error("keyName:" + keyvalues, "Message:'" + e.getMessage() + "'");
            }
        }
    }

    public void setRedisHashMappable(RedisHashMappable redisHashMappable) {
        if (redisHashMappable.redisHashKey() != null) {
            try (Jedis jedis = pool.getResource()) {
                jedis.hmset(redisPrefix + redisHashMappable.redisHashKey(), redisHashMappable.toRedisHashMap());
                jedis.expire(redisPrefix + redisHashMappable.redisHashKey(),
                        redisHashMappable.expireIn() == null ? timeOut : redisHashMappable.expireIn());
            } catch (Exception e) {
                log.error("key:" + redisHashMappable.redisHashKey(), "Message:'" + e.getMessage() + "'");
            }
        }
    }

    public void flushField(
            RedisHashMappable redisHashMappable,
            String... fieldName) {
        if (redisHashMappable.redisHashKey() != null) {
            try (Jedis jedis = pool.getResource()) {
                jedis.hmset(redisPrefix + redisHashMappable.redisHashKey(),
                        redisHashMappable.toRedisHashMap(fieldName));
                jedis.expire(redisPrefix + redisHashMappable.redisKeys(),
                        redisHashMappable.expireIn() == null ? timeOut : redisHashMappable.expireIn());
            } catch (Exception e) {
                log.error("key:" + redisHashMappable.redisHashKey(), "Message:'" + e.getMessage() + "'");
            }
        }
    }

    public <T extends RedisHashMappable> T getRedisHashMappable(Class<T> redisHashMappableClass, String... hashKeyParts) {
        try {
            T t = redisHashMappableClass.getDeclaredConstructor().newInstance();
            try (Jedis jedis = pool.getResource()) {
                String[] array = t.redisKeys().toArray(new String[0]);
                List<String> hmget = jedis.hmget(redisPrefix + t.redisHashKey(hashKeyParts), array);
                if (!hmget.stream().anyMatch(Objects::nonNull))
                    return null;
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < hmget.size(); i++) {
                    map.put(array[i], hmget.get(i));
                }
                t.reloadFromRedisHashMap(map);
                /*jedis.expire(redisPrefix + t.redisHashKey(),
                        t.expireFor() == null ? timeOut : t.expireFor());*/
                return t;
            } catch (Exception e) {
                log.error("keyName:" + t.redisHashKey(), "Message:'" + e.getMessage() + "'");
                throw new RuntimeException(e);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends RedisHashMappable> T removeAndGetRedisHashMappable(Class<T> redisHashMappableClass, String... hashKeyParts) {
        try {
            T t = redisHashMappableClass.getDeclaredConstructor().newInstance();
            try (Jedis jedis = pool.getResource()) {
                String[] array = t.redisKeys().toArray(new String[0]);
                List<String> hmget = jedis.hmget(redisPrefix + t.redisHashKey(hashKeyParts), array);
                if (!hmget.stream().anyMatch(Objects::nonNull))
                    return null;
                Map<String, String> map = new LinkedHashMap<>();
                for (int i = 0; i < hmget.size(); i++) {
                    map.put(array[i], hmget.get(i));
                }
                t.reloadFromRedisHashMap(map);
                jedis.del(redisPrefix + t.redisHashKey(hashKeyParts));
                return t;
            } catch (Exception e) {
                log.error("keyName:" + t.redisHashKey(), "Message:'" + e.getMessage() + "'");
                throw new RuntimeException(e);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends RedisHashMappable> T removeRedisHashMappable(Class<T> redisHashMappableClass, String... hashKeyParts) {
        try {
            T t = redisHashMappableClass.getDeclaredConstructor().newInstance();
            try (Jedis jedis = pool.getResource()) {
                jedis.del(redisPrefix + t.redisHashKey(hashKeyParts));
                return t;
            } catch (Exception e) {
                log.error("keyName:" + t.redisHashKey(), "Message:'" + e.getMessage() + "'");
                throw new RuntimeException(e);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends RedisHashMappable> long removeRedisHashMappable(T redisHashMappable) {
        try {
            try (Jedis jedis = pool.getResource()) {
                return jedis.del(redisPrefix + redisHashMappable.redisHashKey());
            } catch (Exception e) {
                log.error("keyName:" + redisHashMappable.redisHashKey(), "Message:'" + e.getMessage() + "'");
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

