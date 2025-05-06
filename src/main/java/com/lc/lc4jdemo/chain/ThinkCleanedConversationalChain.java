package com.lc.lc4jdemo.chain;

import dev.langchain4j.chain.ConversationalRetrievalChain;

public class ThinkCleanedConversationalChain {

    private final ConversationalRetrievalChain delegate;

    ThinkCleanedConversationalChain(ConversationalRetrievalChain delegate) {
        this.delegate = delegate;
    }

    public String execute(String userMessage) {
        String output = delegate.execute(userMessage);
        return cleanThinkTags(output);
    }

    private String cleanThinkTags(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("(?s)<think>.*?</think>", "").trim();
    }

    public static ThinkCleanedConversationalChain chain(ConversationalRetrievalChain delegate) {
        return new ThinkCleanedConversationalChain(delegate);
    }
}
