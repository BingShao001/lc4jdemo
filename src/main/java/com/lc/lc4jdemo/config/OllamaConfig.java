package com.lc.lc4jdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;

/**
 * Configuration class for Ollama models
 * Provides beans for chat and embedding models
 * 
 * @author bing
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {
    /** Base URL for the Ollama API server */
    @Value("${base-url:http://172.16.2.62:11434}")
    private String baseUrl;
    
    /** Model name to use for Ollama */
    @Value("${model-name:deepseek-r1:32b}")
    private String modelName;

    /**
     * Creates an Ollama chat model for text generation
     * 
     * @return configured OllamaChatModel instance
     */
    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)   // Ollama 默认本地地址
                .modelName(modelName)
                .temperature(0.3)
                .build();
    }
    /**
     * Creates an Ollama embedding model for vector embeddings
     * 
     * @return configured OllamaEmbeddingModel instance
     */
    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)   // Ollama 默认本地地址
                .modelName(modelName)
                .build();
    }
}
