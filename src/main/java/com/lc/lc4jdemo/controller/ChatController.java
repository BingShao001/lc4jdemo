package com.lc.lc4jdemo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lc.lc4jdemo.aiservice.BookService;
import com.lc.lc4jdemo.aiservice.ChannelService;
import com.lc.lc4jdemo.aiservice.ChannelType;
import com.lc.lc4jdemo.aiservice.ChatService;
import com.lc.lc4jdemo.aiservice.FAQService;
import com.lc.lc4jdemo.aiservice.RedisChatService;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

/**
 * Controller for managing chat interactions with different AI services
 * 
 * @author bing
 * @version 1.0
 */
@RestController
public class ChatController {
    @Resource
    private ChatService chatService;
    @Resource
    private RedisChatService redisChatService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ChatMemoryProvider chatRedisMemoryProvider;
    @Resource
    private BookService bookService;
    @Resource
    private FAQService faqService;
    @Resource
    private ChannelService channelService;

    /**
     * Basic chat endpoint
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @return AI response as string
     */
    @GetMapping("/chat")
    public String chat(String userId, String message) {
        return chatService.chat(userId, message);
    }

    /**
     * Stream chat endpoint for real-time token responses
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @return streaming response as Flux of tokens
     */
    @GetMapping(value = "/stream", produces = "text/stream;charset=utf-8")
    public Flux<String> stream(String userId, String message) {
        TokenStream tokenStream = chatService.streamChat(userId, message);
        return Flux.create(sink -> {
            tokenStream.onNext(sink::next).onComplete(aiMessageResponse -> {
                sink.complete();
            }).onError(sink::error).start();
        });
    }

    /**
     * Book service streaming endpoint
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @param date date for booking, defaults to current date if not specified
     * @return streaming response as Flux of tokens
     */
    @GetMapping(value = "/book", produces = "text/stream;charset=utf-8")
    public Flux<String> book(String userId, String message, String date) {
        String dateValue = StringUtils.isBlank(date) ? new SimpleDateFormat("yyyy-MM-dd").format(new Date()) : date;
        TokenStream tokenStream = bookService.streamChat(userId, message, dateValue);
        return Flux.create(sink -> {
            tokenStream.onNext(sink::next).onComplete(aiMessageResponse -> {
                sink.complete();
            }).onError(sink::error).start();
        });
    }
    
    /**
     * FAQ service endpoint
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @return AI response from FAQ service
     */
    @GetMapping("/faq")
    public String faq(String userId, String message) {
        return faqService.chat(userId, message);
    }
    
    /**
     * Channel service endpoint for shopping platform recommendations
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @return recommended shopping channel name
     */
    @GetMapping("/channel")
    public String channel(String userId, String message) {
        ChannelType channelType = channelService.chat(userId, message);
        String name = channelType.getDisplayName();
        System.out.println("shopping channel : " + name);
        return name;
    }

    /**
     * Redis-backed chat service endpoint
     * 
     * @param userId unique identifier for the user
     * @param message message from the user
     * @return AI response from Redis-backed chat service
     */
    @GetMapping("/redis_chat")
    public String redisChat(String userId, String message) {
        return redisChatService.chat(userId, message);
    }

    /**
     * Lists all messages for a user from Redis
     * 
     * @param userId unique identifier for the user
     * @return string representation of all messages
     */
    @GetMapping("/redis_list")
    public String redisList(String userId) {
        String key = String.format("chat_memory:%s", userId);
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        return messages.toString();
    }

    /**
     * Clears chat history for a user
     * 
     * @param userId unique identifier for the user
     * @param message unused parameter
     * @return confirmation message
     */
    @GetMapping("/clear_chat")
    public String clearChat(String userId, String message) {
        chatRedisMemoryProvider.get(userId).clear();
        return "OK";
    }

    /**
     * Clears persistent memory for a user
     * 
     * @param userId unique identifier for the user
     * @return confirmation message
     */
    @GetMapping("/clear_mem")
    public String clearMem(String userId) {
        String key = String.format("chat_persistent_memory:%s", userId);
        redisTemplate.delete(key);
        return "OK";
    }
}