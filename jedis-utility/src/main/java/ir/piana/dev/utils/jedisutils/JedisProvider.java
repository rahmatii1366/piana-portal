package ir.piana.dev.utils.jedisutils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.util.Pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author m.rahmati on 12/31/2024
 */
public abstract class JedisProvider {
    public static Pool<Jedis> getJedisPool(String resourcePath) {
        try (InputStream resourceAsStream = JedisProvider.class.getResourceAsStream(resourcePath)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            return getJedisPool(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Pool<Jedis> getJedisPool(Properties prop) {
        GenericObjectPoolConfig<Jedis> config = new GenericObjectPoolConfig<Jedis>();
        config.setMaxTotal(Integer.parseInt(prop.getProperty("maxTotal", "256")));
        config.setMinIdle(Integer.parseInt(prop.getProperty("minIdle", "16")));
        config.setBlockWhenExhausted(Boolean.parseBoolean(prop.getProperty("blockWhenExhausted", "true")));
        config.setTestOnBorrow(Boolean.parseBoolean(prop.getProperty("testOnBorrow", "true")));
        config.setTestOnCreate(Boolean.parseBoolean(prop.getProperty("testOnCreate", "true")));

        Pool<Jedis> pool = null;
        if (Boolean.parseBoolean(prop.getProperty("isSentinel", "false"))) {
            String sentinelsString = prop.getProperty("sentinels").toString();
            if (sentinelsString.isEmpty())
                throw new RuntimeException("Sentinels is empty");
            List<String> sentinels = Arrays.stream(sentinelsString.split(",")).map(String::trim).toList();
            pool = new JedisSentinelPool(
                    prop.getProperty("master", "master"),
                    new HashSet<>(sentinels),
                    config,
                    prop.getProperty("password"));
        } else {
            pool = new JedisPool(config,
                    prop.getProperty("host"),
                    Integer.parseInt(prop.getProperty("port")),
                    Integer.parseInt(prop.getProperty("timeout")),
                    prop.getProperty("password"));
        }
        return pool;
    }

    public static Pool<Jedis> getJedisPool(JedisPropertyModel propertyModel) {
        if (propertyModel == null) {
            throw new RuntimeException("propertyModel is null");
        } else if (!propertyModel.getSentinel() &&
                (isEmpty(propertyModel.getHost()) || isEmpty(propertyModel.getPort()))) {
            throw new RuntimeException("propertyModel primary attr is null or empty");
        } else if (propertyModel.getSentinel() &&
                (isEmpty(propertyModel.getSentinels()) || isEmpty(propertyModel.getMaster()))) {
            throw new RuntimeException("propertyModel primary attr for sentinel is null or empty");
        }

        Properties properties = new Properties();

        properties.setProperty("maxTotal", Optional.ofNullable(propertyModel.getMaxTotal()).orElse(256).toString());
        properties.setProperty("minIdle", Optional.ofNullable(propertyModel.getMinIdle()).orElse(16).toString());
        properties.setProperty("blockWhenExhausted", Optional.ofNullable(propertyModel.getBlockWhenExhausted()).orElse(true).toString());
        properties.setProperty("testOnBorrow", Optional.ofNullable(propertyModel.getTestOnBorrow()).orElse(true).toString());
        properties.setProperty("testOnCreate", Optional.ofNullable(propertyModel.getTestOnCreate()).orElse(true).toString());
        properties.setProperty("isSentinel", String.valueOf(propertyModel.getSentinel()));
        properties.setProperty("sentinels", Optional.ofNullable(propertyModel.getSentinels()).orElse(""));
        properties.setProperty("master", Optional.ofNullable(propertyModel.getMaster()).orElse(""));
        if (propertyModel.getPassword() != null)
            properties.setProperty("password", propertyModel.getPassword());
        properties.setProperty("host", Optional.ofNullable(propertyModel.getHost()).orElse(""));
        properties.setProperty("port", Optional.ofNullable(propertyModel.getPort()).orElse(0).toString());
        properties.setProperty("timeout", Optional.ofNullable(propertyModel.getTimeout()).orElse(10000).toString());

        return getJedisPool(properties);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    public static boolean isEmpty(final Integer cs) {
        return cs == null || cs < 0;
    }
}
