package com.lc.lc4jdemo.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import retrofit2.http.Streaming;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,  // 启用显式模式
        streamingChatModel = "qwenStreamingChatModel", chatModel = "qwenChatModel", chatMemoryProvider = "chatMemoryProvider",tools = {"bookTools"}) // 自动注入配置
public interface BookService {


    // 流式响应示例
    @SystemMessage("你是一个预定机票的小助手，当用户打招呼时，礼貌的介绍自己是一个订机票的小助手，可以为用户提供订票服务。" +
            "需要让用户提供一下航班信息和姓名。" +
            "预定的日期为 {{currentDate}}")
    @Streaming
    TokenStream streamChat(@MemoryId String sessionId, @UserMessage String message, @V("currentDate")String  currentDate);


}

