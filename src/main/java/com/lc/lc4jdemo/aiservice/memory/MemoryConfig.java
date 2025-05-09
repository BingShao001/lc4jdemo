package com.lc.lc4jdemo.aiservice.memory;

import com.lc.lc4jdemo.redis.FuryByteRedisSerializer;
import com.lc.lc4jdemo.redis.RedisFurySvc;
import dev.ai4j.openai4j.chat.ToolCall;
import dev.ai4j.openai4j.chat.ToolMessage;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class MemoryConfig {
    private static final String REDIS_KEY_FORMAT = "chat_persistent_memory:%s";

    // 按 memoryId 动态生成隔离的 ChatMemory
    @Bean
    public ChatMemoryProvider chatMemoryProvider(RedisFurySvc redisFurySvc) {
        return memoryId -> MessageWindowChatMemory.builder().id(memoryId) // 根据 memoryId 区分会话
                .maxMessages(10)
                //可以直接存储DB或者Redis（这样实现更符合官方框架）
                .chatMemoryStore(new ChatMemoryStore() {
                    @Override
                    public List<ChatMessage> getMessages(Object memoryId) {
                        String key = String.format(REDIS_KEY_FORMAT, memoryId);
                        List<ChatMessage> messages = redisFurySvc.get(key, List.class);
                        if (messages == null || messages.isEmpty()) {
                            return new ArrayList<>();
                        }

                        Set<String> validToolCallIds = new HashSet<>();
                        // 提取所有有效的 tool_call_id
                        for (ChatMessage msg : messages) {
                            if (msg instanceof AiMessage aiMsg && aiMsg.hasToolExecutionRequests()) {
                                for (ToolExecutionRequest call : aiMsg.toolExecutionRequests()) {
                                    validToolCallIds.add(call.id());
                                }
                            }
                        }

                        // 过滤非法的 ToolMessage
                        List<ChatMessage> cleanedMessages = messages.stream().filter(msg -> {
                            if (msg instanceof ToolExecutionResultMessage toolMsg) {
                                boolean isValid = validToolCallIds.contains(toolMsg.id());
                                if (!isValid) {
                                    log.warn("移除非法 ToolMessage，toolCallId: {}", toolMsg.id());
                                }
                                return isValid;
                            }
                            return true;
                        }).collect(Collectors.toList());

                        return cleanedMessages;
                    }

                    @Override
                    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
                        if (messages == null || messages.isEmpty()) {
                            log.info("No messages to update for memoryId: {}", memoryId);
                            return;
                        }

                        // 可选：防止空 AiMessage 保存
                        messages.removeIf(msg -> (msg instanceof AiMessage ai && ai.text() == null));

                        String key = String.format(REDIS_KEY_FORMAT, memoryId);
                        redisFurySvc.set(key, messages, 7, TimeUnit.DAYS);
                    }

                    @Override
                    public void deleteMessages(Object memoryId) {
                        redisFurySvc.del(String.format("chat_persistent_memory:%s", memoryId));
                    }
                }).build();
    }

    /**
     * 这个是自己实现的持久化Memory，这种方式更新性能更好，但是需要自己更多的改动，有bug风险
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ChatMemoryProvider chatRedisMemoryProvider(RedisTemplate<String, Object> redisTemplate) {

        return memoryId -> ChatRedisMemory.builder().redisTemplate(redisTemplate).memoryId(memoryId).build();
    }

    @Bean
    public ChatMemory globalMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }
}
