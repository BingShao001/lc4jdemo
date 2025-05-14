package com.lc.lc4jdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;

/**
 * Configuration class for Qwen models from Alibaba Cloud
 * Provides beans for both standard and streaming chat models
 * 
 * @author bing
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "qwen")
public class QwenConfig {

    /** API key for accessing Qwen models */
    @Value("${api-key:sk-6e710e765f8e4b46b8483bfcf38ccecb}")
    private String apiKey;
    
    /** Base URL for the API */
    @Value("${base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    /**
     * Creates a Qwen chat model for text generation
     * 
     * @return configured QwenChatModel instance
     */
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

    /**
     * Creates a Qwen streaming chat model for token-by-token generation
     * 
     * @return configured QwenStreamingChatModel instance
     */
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
