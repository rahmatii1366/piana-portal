package ir.piana.dev.utils.jedisutils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(prefix = "piana.tools.redis-cache", name = "enabled", havingValue = "true", matchIfMissing = false)
public class JedisAutoConfiguration {
    @Component
    @ConfigurationProperties(prefix = "piana.tools.redis-cache")
    public static class JedisProperties extends JedisPropertyModel {
    }

    @Bean
    JedisPool jedisPool(JedisProperties jedisProperties) {
        Pool<Jedis> jedisPool = JedisProvider.getJedisPool(jedisProperties);
        String prefix = Optional.ofNullable(jedisProperties.getPrefix()).orElse("piana.jedis");
        prefix = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
        return new JedisPool(jedisPool, prefix,
                Optional.ofNullable(jedisProperties.getTimeout()).orElse(30 * 60));
    }
}
