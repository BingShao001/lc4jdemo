package com.lc.lc4jdemo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "model.base.url")
public class LangChainConfig {
    @Value("${openai.api-key:demo}")
    private String apiKey;

    @Bean
    public ChatLanguageModel chatModel() {
        return OpenAiChatModel.builder().apiKey(apiKey).baseUrl("https://api.openai.com/v1").modelName(OpenAiChatModelName.GPT_4_O_MINI).maxTokens(1000).topP(0.1).temperature(0.3).build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder().baseUrl("http://172.16.2.62:11434")   // Ollama 默认本地地址
                .modelName("deepseek-r1:32b").temperature(0.3).build();
    }

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder().baseUrl("http://172.16.2.62:11434")   // Ollama 默认本地地址
                .modelName("deepseek-r1:32b").build();
    }


}

