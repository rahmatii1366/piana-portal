package ir.piana.dev.utils.jedisutils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author m.rahmati on 1/2/2025
 */
public interface RedisHashMappable extends Serializable {
    Map<String, String> toRedisHashMap();
    default Map<String, String> toRedisHashMap(String... fieldNames) {
        List<String> list = Arrays.asList(fieldNames);
        return toRedisHashMap().entrySet().stream().filter(e -> list.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), Map.Entry::getValue));
    }
    void reloadFromRedisHashMap(Map<String, String> redisHashMap);
    List<String> redisKeys();
    String redisHashKey(String... hashKeys);
    /**
     * @return timeout expiration at milliseconds
     * */
    default Long expireIn() {
        return null;
    }

//    default void removeField(JedisPool jedisPool, String fieldName) {
//        /*if (fieldName instanceof RedisHashMapChangeable<?>) {
//            ((RedisHashMapChangeable) fieldName).remove(jedisPool, this);
//        }*/
//        try {
//            Field declaredField = this.getClass().getDeclaredField(fieldName);
//            if (declaredField.getDeclaringClass().isAssignableFrom(List.class))
//                declaredField.set(this, new ArrayList<>());
//            else
//                declaredField.set(this, new ArrayList<>());
//            jedisPool.removeKey(redisHashKey(), fieldName);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /*default void changeField(JedisPool jedisPool, Enum<?> fieldName, Object value) {
        if (fieldName instanceof RedisHashMapChangeable<?>) {
            ((RedisHashMapChangeable) fieldName).setValue(jedisPool, this, new Stringer(value));
        }
    }*/
}
