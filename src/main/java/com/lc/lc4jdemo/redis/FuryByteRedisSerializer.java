package com.lc.lc4jdemo.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;


/**
 * @author xinruifan
 * @create 2024-09-04 10:09
 */
@Component
@Slf4j
public class FuryByteRedisSerializer implements RedisSerializer<Object> {

    private static final ThreadSafeFury fury;

    static {
        fury = Fury.builder()
                .withLanguage(Language.JAVA)
                .withRefTracking(true)
                .requireClassRegistration(false)
                .withNumberCompressed(false)
                .buildThreadLocalFury();
    }


    @Override
    public byte[] serialize(Object o) throws SerializationException {
        try {
            return fury.serialize(o);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize object", ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return fury.deserialize(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize object", ex);
        }
    }
}
