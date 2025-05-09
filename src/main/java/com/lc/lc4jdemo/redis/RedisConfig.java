package com.lc.lc4jdemo.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.lc.lc4jdemo.redis.jackson.AiMessageMixin;
import com.lc.lc4jdemo.redis.jackson.SystemMessageMixin;
import com.lc.lc4jdemo.redis.jackson.TextContentMixin;
import com.lc.lc4jdemo.redis.jackson.ToolExecutionResultMessageMixin;
import com.lc.lc4jdemo.redis.jackson.UserMessageMixin;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplateJson(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key 序列化
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        // value 序列化
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 注册 MixIn
        mapper.addMixIn(UserMessage.class, UserMessageMixin.class);
        mapper.addMixIn(SystemMessage.class, SystemMessageMixin.class);
        mapper.addMixIn(TextContent.class, TextContentMixin.class);
        mapper.addMixIn(AiMessage.class, AiMessageMixin.class);
        mapper.addMixIn(ToolExecutionResultMessage.class, ToolExecutionResultMessageMixin.class);

        // 你可能还需要为 AssistantMessage、ToolMessage 等其他子类加 Mixin

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // key 序列化
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(RedisSerializer.byteArray());
        template.setHashKeySerializer(keySerializer);
        template.afterPropertiesSet();
        return template;
    }
}