package com.lc.lc4jdemo.chain;


public class MarkdownJsonCleanConversationalChain {

    private final ThinkCleanedConversationalChain delegate;

    MarkdownJsonCleanConversationalChain(ThinkCleanedConversationalChain delegate) {
        this.delegate = delegate;
    }

    public String execute(String userMessage) {
        String output = delegate.execute(userMessage);
        return cleanMarkdownJson(output);
    }

    private String cleanMarkdownJson(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1").trim();
    }

    public static MarkdownJsonCleanConversationalChain chain(ThinkCleanedConversationalChain delegate) {
        return new MarkdownJsonCleanConversationalChain(delegate);
    }

}
