package com.lc.lc4jdemo.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {
    @Value("${base-url:http://172.16.2.62:11434}")
    private String baseUrl;
    @Value("${model-name:deepseek-r1:32b}")
    private String modelName;

    //大模型
    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)   // Ollama 默认本地地址
                .modelName(modelName)
                .temperature(0.3)
                .build();
    }
    //向量模型
    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)   // Ollama 默认本地地址
                .modelName(modelName)
                .build();
    }
}
