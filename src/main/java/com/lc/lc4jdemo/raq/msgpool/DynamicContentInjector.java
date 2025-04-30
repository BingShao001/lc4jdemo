package com.lc.lc4jdemo.raq.msgpool;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class DynamicContentInjector extends DefaultContentInjector {

    private final DynamicPromptTemplateProvider promptTemplateProvider;

    public DynamicContentInjector(DynamicPromptTemplateProvider provider) {
        this.promptTemplateProvider = provider;
    }

    @Override
    protected Prompt createPrompt(UserMessage userMessage, List<Content> contents) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userMessage", userMessage.singleText());
        variables.put("contents", format(contents));
        System.out.println(contents);
        return promptTemplateProvider.getPromptTemplate().apply(variables);
    }
}

