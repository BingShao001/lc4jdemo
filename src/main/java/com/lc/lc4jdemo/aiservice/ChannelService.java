package com.lc.lc4jdemo.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import retrofit2.http.Streaming;


@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "qwenChatModel", chatMemoryProvider = "chatMemoryProvider")
public interface ChannelService {

    @SystemMessage("请根据用户的购物意图分析应选择哪个购物平台，只能从：小红书、淘宝、京东、拼多多、唯品会 中选择。")
    ChannelType chat(@MemoryId String sessionId, @UserMessage String message);
}
