package com.lc.lc4jdemo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {
    @Value("${api-key:demo}")
    private String apiKey;
    @Value("${base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Bean
    public ChatLanguageModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .maxTokens(1000)
                .topP(0.1)
                .temperature(0.3)
                .build();
    }
}
