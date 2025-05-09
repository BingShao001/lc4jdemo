package com.lc.lc4jdemo.aiservice.memory;

import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ChatRedisMemory implements ChatMemory {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Object memoryId;

    public ChatRedisMemory(RedisTemplate<String, Object> redisTemplate, Object memoryId) {
        this.redisTemplate = redisTemplate;
        this.memoryId = memoryId;
    }

    @Override
    public Object id() {
        return memoryId;
    }

    @Override
    public void add(ChatMessage message) {
        String key = String.format("chat_memory:%s", memoryId);
        boolean isSystemMessage = message instanceof SystemMessage;
        if (isSystemMessage && hasHistorySystemMessage(key)) {
            return;
        }
        // 将消息添加到 Redis 列表中（从左侧添加
        redisTemplate.opsForList().rightPush(key, message);
        // 自动保留最近10条消息
        redisTemplate.opsForList().trim(key, -10, -1);
    }

    private boolean hasHistorySystemMessage(String key) {
        // 检查是否已存在 SystemMessage（只查最近10条）
        List<Object> history = redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtils.isEmpty(history)) {
            return false;
        }
        return history.stream().anyMatch(m -> {
            if (m instanceof SystemMessage) {
                return true;
            }
            if (m instanceof ChatMessage) {
                return ((ChatMessage) m).type() == ChatMessageType.SYSTEM;
            }
            return false;
        });

    }

    @Override
    public List<ChatMessage> messages() {
        String key = String.format("chat_memory:%s", memoryId);
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        return messages.stream().map(msg -> (ChatMessage) msg) // 确保这里的类型转换是安全的
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        String key = String.format("chat_memory:%s", memoryId);
        redisTemplate.delete(key);
    }
}