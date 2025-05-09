package com.lc.lc4jdemo.raq.faq;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationalChainFactory {

    private final OllamaChatModel chatModel;
    private final ContentRetriever retriever;
    private final Map<String, ChatMemory> memoryMap = new ConcurrentHashMap<>();

    public ConversationalChainFactory(OllamaChatModel chatModel, ContentRetriever retriever) {
        this.chatModel = chatModel;
        this.retriever = retriever;
    }

    public ConversationalRetrievalChain getChainForChatId(String userId) {
        ChatMemory memory = memoryMap.computeIfAbsent(userId, id -> MessageWindowChatMemory.withMaxMessages(10));
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(chatModel)
                .chatMemory(memory)
                .contentRetriever(retriever)
                .build();
    }
}
