package com.lc.lc4jdemo.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import retrofit2.http.Streaming;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,  // 启用显式模式
        streamingChatModel = "qwenStreamingChatModel", chatModel = "qwenChatModel", chatMemoryProvider = "chatMemoryProvider",tools = {"weatherTools"}) // 自动注入配置
public interface ChatService {

    @SystemMessage("你是一个礼貌的助手，回答前先自我介绍")
    String chat(@MemoryId String sessionId, @UserMessage String message);

    // 流式响应示例
    @SystemMessage("逐词输出思考过程")
    @Streaming
    TokenStream streamChat(@MemoryId String sessionId, @UserMessage String message);


}

