package com.lc.lc4jdemo.config;

import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "qwen")
public class QwenConfig {

    @Value("${api-key:sk-6e710e765f8e4b46b8483bfcf38ccecb}")
    private String apiKey;
    @Value("${base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Bean
    QwenChatModel qwenChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
//                .baseUrl(baseUrl)
                .modelName("qwen-max")
                .maxTokens(4096)
                .temperature(0.2F)
                .build();
    }

    @Bean
    QwenStreamingChatModel qwenStreamingChatModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-plus")
                .maxTokens(4096)
                .temperature(0.2F)
                .build();
    }

}
