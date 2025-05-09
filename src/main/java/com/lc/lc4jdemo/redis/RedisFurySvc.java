package com.lc.lc4jdemo.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lijian 2022/4/16
 */
@Slf4j
@Component
public class RedisFurySvc {

    private final RedisTemplate redisTpl;
    private final FuryByteRedisSerializer fury;


    public RedisTemplate redisTemplate() {
        return redisTpl;
    }

    @Autowired
    public RedisFurySvc(@Qualifier("redisTemplate") RedisTemplate redisTpl,
                        FuryByteRedisSerializer fury) {
        this.redisTpl = redisTpl;
        this.fury = fury;
    }

    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTpl.expire(key, timeout, timeUnit);
    }

    public Boolean expireAt(String key, Instant instant) {
        return redisTpl.expireAt(key, instant);
    }


    /********--------- String ------------*********/

    public <T> void set(String key, T val) {
        redisTpl.opsForValue().set(key, fury.serialize(val));
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(fury.deserialize((byte[]) redisTpl.opsForValue().get(key)));
    }

    public <T> T getV2(String key, Class<T> clazz) {
        Object res = redisTpl.opsForValue().get(key);
        if (Objects.isNull(res)) {
            return null;
        }

        return clazz.cast(fury.deserialize((byte[]) redisTpl.opsForValue().get(key)));
    }

    public <T> void set(String key, T val, long timeout, TimeUnit timeUnit) {
        redisTpl.opsForValue().set(key, fury.serialize(val), timeout, timeUnit);
    }


    public <T> Boolean setIfAbsent(String key, T val, long timeout, TimeUnit timeUnit) {
        return redisTpl.opsForValue().setIfAbsent(key, fury.serialize(val), timeout, timeUnit);
    }

    public <T> void set(String key, T val, Duration timeout) {
        redisTpl.opsForValue().set(key, fury.serialize(val), timeout);
    }


    /********--------- zSet ------------*********/

    public <T> void zadd(String key, T val, double score) {
        redisTpl.opsForZSet().add(key, fury.serialize(val), score);
    }

    public <T> void zaddBatch(String key, Set<ZSetOperations.TypedTuple<T>> tupleSet) {
        Set<ZSetOperations.TypedTuple<byte[]>> serializedTupleSet = tupleSet.stream()
                .map(tuple -> ZSetOperations.TypedTuple.of(fury.serialize(tuple.getValue()), tuple.getScore()))
                .collect(Collectors.toSet());
        redisTpl.opsForZSet().add(key, serializedTupleSet);
    }

    public <T> Double zIncrementScore(String key, T val, double score) {
        return redisTpl.opsForZSet().incrementScore(key, fury.serialize(val), score);
    }

    public <T> Double zScore(String key, T val) {
        return redisTpl.opsForZSet().score(key, fury.serialize(val));
    }

    public Long zCount(String key, double min, double max) {
        return redisTpl.opsForZSet().count(key, min, max);
    }


    public Long zSize(String key) {
        Long size = redisTpl.opsForZSet().size(key);
        return size == null ? 0 : size;
    }


    public void zrem(String key, Object... val) {
        redisTpl.opsForZSet().remove(key, val);
    }

    @SuppressWarnings("unchecked")
    public <T> Set zrangeByScore(String key, double min, double max, Class<T> clazz) {
        Set result = redisTpl.opsForZSet().rangeByScore(key, min, max);
        if (result == null) {
            return Collections.emptySet();
        }
        result.stream().map(x -> {
            if (clazz.isInstance(x)) {
                return clazz.cast(x);
            }
            if (x instanceof byte[]) {
                return clazz.cast(fury.deserialize((byte[]) x));
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }


    @SuppressWarnings("unchecked")
    public <T> Set zrange(String key, long start, long end, Class<T> clazz) {
        Set result = redisTpl.opsForZSet().range(key, start, end);
        if (result == null) {
            return Collections.emptySet();
        }
        result.stream().map(x -> {
            if (clazz.isInstance(x)) {
                return clazz.cast(x);
            }
            if (x instanceof byte[]) {
                return clazz.cast(fury.deserialize((byte[]) x));
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }

    public Long zRemByScore(String key, double min, double max) {
        return redisTpl.opsForZSet().removeRangeByScore(key, min, max);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<ZSetOperations.TypedTuple<T>> zrangeByScoreWithScores(String key, double min, double max, Class<T> clazz) {
        Set<ZSetOperations.TypedTuple<T>> result = redisTpl.opsForZSet().rangeByScoreWithScores(key, min, max);

        if (result == null || result.isEmpty()) {
            return Collections.emptySet();
        }
        result.stream().map(tuple -> {
            T rawValue = tuple.getValue();
            if (clazz.isInstance(rawValue)) {
                return clazz.cast(rawValue);
            }
            if (rawValue instanceof byte[]) {
                T deserializedValue = clazz.cast(fury.deserialize((byte[]) rawValue));
                return ZSetOperations.TypedTuple.of(deserializedValue, tuple.getScore());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> zrangeByScore(String key, double min, double max, long offset, long size, Class<T> clazz) {
        Set result = redisTpl.opsForZSet().rangeByScore(key, min, max, offset, size);
        if (result == null) {
            return Collections.emptySet();
        }
        result.stream().map(x -> {
            if (clazz.isInstance(x)) {
                return clazz.cast(x);
            }
            if (x instanceof byte[]) {
                return clazz.cast(fury.deserialize((byte[]) x));
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Long zremRangeByScore(String key, double min, double max) {
        return redisTpl.opsForZSet().removeRangeByScore(key, min, max);
    }

    /********--------- Hash ------------*********/


    @SuppressWarnings("unchecked")
    public <K, T> void hset(String key, K hashKey, T val) {
        redisTpl.opsForHash().put(key, hashKey, fury.serialize(val));
    }

    @SuppressWarnings("unchecked")
    public <K, T> void hset(String key, K hashKey, T val, long timeout, TimeUnit timeUnit) {
        redisTpl.opsForHash().put(key, hashKey, fury.serialize(val));
        redisTpl.expire(key, timeout, timeUnit);
    }

    public void hPutAll(String key, Map<?, ?> map) {
        Map<Object, Object> serializedMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            serializedMap.put(entry.getKey(), fury.serialize(entry.getValue()));
        }
        redisTpl.opsForHash().putAll(key, serializedMap);
    }

    public <T> Map<Object, T> hEntries(String key, Class<T> clazz) {
        Map<Object, T> resMap = new HashMap<>();
        Map<Object, Object> entries = redisTpl.opsForHash().entries(key);
        for (Map.Entry<?, ?> entry : entries.entrySet()) {
            resMap.put(entry.getKey(), clazz.cast(fury.deserialize((byte[]) entry.getValue())));
        }
        return resMap;
    }

//    public Long hdel(String key, Object... hashKeys) {
//        return redisTpl.opsForHash().delete(key, hashKeys);
//    }

    public void hPutAll(String key, Map<?, ?> map, long timeout, TimeUnit timeUnit) {
        Map<Object, Object> serializedMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            serializedMap.put(entry.getKey(), fury.serialize(entry.getValue()));
        }
        redisTpl.opsForHash().putAll(key, serializedMap);
        expire(key, timeout, timeUnit);
    }

    @SuppressWarnings("unchecked")
    public <T, K> T hget(String key, K hashKey, Class<T> clazz) {
        return clazz.cast(fury.deserialize((byte[]) redisTpl.opsForHash().get(key, hashKey)));
    }

    @SuppressWarnings("unchecked")
//    public <T> List<T> multiGet(String key, Collection<String> hashKeys, Class<T> clazz) {
//        return (List<T>) redisTpl.opsForHash().multiGet(key, hashKeys).stream().map(x -> clazz.cast(fury.deserialize((byte[]) x))).collect(Collectors.toList());
//
//    }

    public <K> Long hincr(String key, K hashKey) {
        return redisTpl.opsForHash().increment(key, hashKey, 1);
    }

    public <K> Long incr(String key) {
        return redisTpl.opsForValue().increment(key, 1);
    }

    public <K> Long incrWithDelta(String key, long delta) {
        return redisTpl.opsForValue().increment(key, delta);
    }

    public <K> Long hincr(String key, K hashKey, long step) {
        return redisTpl.opsForHash().increment(key, hashKey, step);
    }

    public <K> Long hincr(String key, K hashKey, long step, long timeout, TimeUnit timeUnit) {
        Long increment = redisTpl.opsForHash().increment(key, hashKey, step);
        redisTpl.expire(key, timeout, timeUnit);
        return increment;
    }


    /********--------- List ------------*********/

    public <T> Long lPushAll(String key, List<T> values) {
        return redisTpl.opsForList().leftPushAll(key, values.stream().map(x -> fury.serialize(x)).collect(Collectors.toList()));
    }

    public <T> Long lPushAll(String key, List<T> values, long timeout, TimeUnit timeUnit) {
        Long rs = redisTpl.opsForList().leftPushAll(key, values.stream().map(x -> fury.serialize(x)).collect(Collectors.toList()));
        redisTpl.expire(key, timeout, timeUnit);
        return rs;
    }

    public <T> Long rPushAll(String key, List<T> values, long timeout, TimeUnit timeUnit) {
        Long rs = redisTpl.opsForList().rightPushAll(key, values.stream().map(x -> fury.serialize(x)).collect(Collectors.toList()));
        redisTpl.expire(key, timeout, timeUnit);
        return rs;
    }

    public <T> Long rPushAll(String key, List<T> values) {
        Long rs = redisTpl.opsForList().rightPushAll(key, values.stream().map(x -> fury.serialize(x)).collect(Collectors.toList()));
        return rs;
    }

    public <T> List<T> leftPop(String key, long count, Class<T> clazz) {
        return (List<T>) redisTpl.opsForList().leftPop(key, count).stream().map(x -> clazz.cast(fury.deserialize((byte[]) x))).collect(Collectors.toList());
    }


    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        return (List<T>) redisTpl.opsForList().range(key, start, end).stream().map(x -> clazz.cast(fury.deserialize((byte[]) x))).collect(Collectors.toList());
    }

    public <T> Long lPush(String key, T value) {
        return redisTpl.opsForList().leftPush(key, value);
    }

    public <T> T rPop(String key, Class<T> clazz) {
        return clazz.cast(fury.deserialize((byte[]) redisTpl.opsForList().rightPop(key)));
    }

    public <T> List<T> rPop(String key, Long count, Class<T> clazz) {
        return (List<T>) redisTpl.opsForList().rightPop(key, count).stream().map(x -> clazz.cast(fury.deserialize((byte[]) x))).collect(Collectors.toList());
    }

    public long lSize(String key) {
        final Long size = redisTpl.opsForList().size(key);
        return size == null ? 0 : size;
    }

    public boolean setBit(String key, long offset, boolean b) {
        return redisTpl.opsForValue().setBit(key, offset, b);
    }

    public boolean getBit(String key, long offset) {
        return redisTpl.opsForValue().getBit(key, offset);
    }

    public <T> boolean hHasKey(String key, T hashKey) {
        return redisTpl.opsForHash().hasKey(key, hashKey);
    }

    public <T> boolean hasKey(String key) {
        return redisTpl.hasKey(key);
    }

    public Boolean del(String key) {
        return redisTpl.delete(key);
    }

    public <T> void publish(String channel, T msg) {
        redisTpl.convertAndSend(channel, msg);
    }

    public Long ttl(String key) {
        return redisTpl.getExpire(key);
    }

    /********--------- Set ------------*********/

    public long sSize(String key) {
        final Long size = redisTpl.opsForSet().size(key);
        return size == null ? 0 : size;
    }

    @SuppressWarnings("unchecked")
    public <T> Long sadd(String key, T... val) {
        return redisTpl.opsForSet().add(key, Arrays.stream(val)
                .map(fury::serialize).toArray());
    }

    public <T> T spop(String key, Class<T> clazz) {
        return clazz.cast(fury.deserialize((byte[]) redisTpl.opsForSet().pop(key)));
    }

    @SuppressWarnings("unchecked")
    public <T> Set members(String key, Class<T> clazz) {
        return (Set) redisTpl.opsForSet().members(key).stream().map(x -> clazz.cast(fury.deserialize((byte[]) x))).collect(Collectors.toSet());
    }

    public Map<Object, Boolean> isMembers(String key, Object... objects) {
        return redisTpl.opsForSet().isMember(key, Arrays.stream(objects).map(fury::serialize).toArray());
    }

    public Long srem(String key, Object... values) {
        return redisTpl.opsForSet().remove(key, Arrays.stream(values).map(fury::serialize).toArray());
    }

    public <T> Set<ZSetOperations.TypedTuple<T>> zrangeWithScores(String key, long start, long end, Class<T> clazz) {
        Set<ZSetOperations.TypedTuple<T>> result = redisTpl.opsForZSet().rangeWithScores(key, start, end);

        if (result == null || result.isEmpty()) {
            return Collections.emptySet();
        }
        result.stream().map(tuple -> {
            T rawValue = tuple.getValue();
            if (clazz.isInstance(rawValue)) {
                return clazz.cast(rawValue);
            }
            if (rawValue instanceof byte[]) {
                T deserializedValue = clazz.cast(fury.deserialize((byte[]) rawValue));
                return ZSetOperations.TypedTuple.of(deserializedValue, tuple.getScore());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }

    public <T> Set<ZSetOperations.TypedTuple<T>> zReverseRangeWithScores(String key, long start, long end, Class<T> clazz) {
        Set<ZSetOperations.TypedTuple<T>> result = redisTpl.opsForZSet().reverseRangeWithScores(key, start, end);

        if (result == null || result.isEmpty()) {
            return Collections.emptySet();
        }
        result.stream().map(tuple -> {
            T rawValue = tuple.getValue();
            if (clazz.isInstance(rawValue)) {
                return clazz.cast(rawValue);
            }
            if (rawValue instanceof byte[]) {
                T deserializedValue = clazz.cast(fury.deserialize((byte[]) rawValue));
                return ZSetOperations.TypedTuple.of(deserializedValue, tuple.getScore());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return result;
    }

    public long zRemRangeByRank(String key, long start, long end) {
        return redisTpl.opsForZSet().removeRange(key, start, end);
    }

    public long zCard(String key) {
        return redisTpl.opsForZSet().zCard(key);
    }

    public boolean zRank(String key, Object o) {
        return redisTpl.opsForZSet().rank(key, o) != null;
    }

//    public List<?> executePipelined(RedisCallback<?> action) {
//        return redisTpl.executePipelined(action);
//    }

    public boolean isMember(String key, Object value) {
        return redisTpl.opsForSet().isMember(key, value);
    }

    public void hdel(String key, Object hKey) {
        redisTpl.opsForHash().delete(key, hKey);
    }
}
