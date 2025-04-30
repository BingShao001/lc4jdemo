package com.lc.lc4jdemo.controller;

import com.lc.lc4jdemo.config.MarkdownJsonCleanConversationalChain;
import com.lc.lc4jdemo.config.ThinkCleanedConversationalChain;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AIController
 *
 * @author bing
 * @version 1.0
 * @create 2025/4/25
 **/
@RestController
@Slf4j
public class AIController {

    @Resource
    private OllamaChatModel ollamaChatModel;
    @Resource
    private ConversationalRetrievalChain faqRagChain;
    @Resource
    private ConversationalRetrievalChain knowledgeRagChain;
    @Resource
    private MarkdownJsonCleanConversationalChain msgPoolRagChain;


    @GetMapping("/ask")
    public String ask(String question) {
        return ollamaChatModel.generate(question);
    }

    @GetMapping("/ask_faq")
    public String askFaq(String question) {
        return faqRagChain.execute(question);
    }

    @GetMapping("/ask_knowledge")
    public String askKnowledge(String question) {
        return knowledgeRagChain.execute(question);
    }

    @GetMapping("/ask_msg")
    public String ask_msg(String question) {
        log.info(question);
        String answer = msgPoolRagChain.execute(question);
        answer = answer.replaceAll("(?s)<think>.*?</think>", "").trim();
        log.info(answer);
        return answer;
    }
}
