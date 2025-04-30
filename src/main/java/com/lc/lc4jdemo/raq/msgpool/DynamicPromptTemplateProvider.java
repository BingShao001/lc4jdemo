package com.lc.lc4jdemo.raq.msgpool;

import dev.langchain4j.model.input.PromptTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DynamicPromptTemplateProvider {
    @Value("${prompt.template.path:/Users/bing/Documents/msg_pool_prompt.md}")
    private String promptTemplatePath;
    private volatile PromptTemplate promptTemplate;

    @PostConstruct
    public void init() {
        reloadPrompt();
    }

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void reloadPrompt() {
        try {
            String promptText = Files.readString(Paths.get(promptTemplatePath), StandardCharsets.UTF_8);
            promptTemplate = PromptTemplate.from(promptText);
            log.info("reload prompt success prompt path {}", promptTemplatePath);
        } catch (IOException e) {
            log.error("reload prompt error path : {} ", promptTemplatePath, e);
        }

    }

    public PromptTemplate getPromptTemplate() {
        return promptTemplate;
    }
}
