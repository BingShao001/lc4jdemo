package com.lc.lc4jdemo.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import retrofit2.http.Streaming;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,  // 启用显式模式
        streamingChatModel = "qwenStreamingChatModel", chatModel = "ollamaChatModel", chatMemoryProvider = "chatMemoryProvider",contentRetriever = "faqContentRetriever") // 自动注入配置
public interface FAQService {

    @SystemMessage("你是一个礼貌的问答助手，可以回答关于jagat的任何问题")
    String chat(@MemoryId String sessionId, @UserMessage String message);



}

