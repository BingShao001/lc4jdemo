package com.lc.lc4jdemo.controller;

import com.lc.lc4jdemo.aiservice.ChatService;
import com.lc.lc4jdemo.aiservice.RedisChatService;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class ChatController {
    @Resource
    private ChatService chatService;
    @Resource
    private RedisChatService redisChatService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private ChatMemoryProvider chatRedisMemoryProvider;


    @GetMapping("/chat")
    public String chat(String userId, String message) {
        return chatService.chat(userId, message);
    }

    // 正确示例：返回 Flux
    @GetMapping(value = "/stream", produces = "text/stream;charset=utf-8")
    public Flux<String> stream(String userId, String message) {
        TokenStream tokenStream = chatService.streamChat(userId, message);
        return Flux.create(sink -> {
            tokenStream.onNext(sink::next).onComplete(aiMessageResponse -> {
                sink.complete();
            }).onError(sink::error).start();
        });
    }

    @GetMapping("/redis_chat")
    public String redisChat(String userId, String message) {
        return redisChatService.chat(userId, message);
    }

    @GetMapping("/redis_list")
    public String redisList(String userId) {
        String key = String.format("chat_memory:%s", userId);
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        return messages.toString();
    }
    @GetMapping("/clear_chat")
    public String clearChat(String userId, String message) {
        chatRedisMemoryProvider.get(userId).clear();
        return "OK";
    }
    @GetMapping("/clear_mem")
    public String clearMem(String userId) {
        String key = String.format("chat_persistent_memory:%s", userId);
        redisTemplate.delete(key);
        return "OK";
    }
}