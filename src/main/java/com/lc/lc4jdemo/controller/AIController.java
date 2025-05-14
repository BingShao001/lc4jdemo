package com.lc.lc4jdemo.controller;

import com.lc.lc4jdemo.chain.MarkdownJsonCleanConversationalChain;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * AIController - Handles direct AI model interactions and RAG chains
 *
 * @author bing
 * @version 1.0
 * @create 2025/4/25
 */
@RestController
@Slf4j
public class AIController {

    @Resource
    private OllamaChatModel ollamaChatModel;
    
    @Resource
    private QwenChatModel qwenChatModel;
    
    @Resource
    private ConversationalRetrievalChain faqRagChain;
    
    @Resource
    private ConversationalRetrievalChain knowledgeRagChain;
    
    @Resource
    private MarkdownJsonCleanConversationalChain msgPoolRagChain;

    @Resource
    private QwenStreamingChatModel qwenStreamingChatModel;

    /**
     * Streaming question answering endpoint using Qwen model
     *
     * @param question the user's question
     * @return streaming response as a Flux of tokens
     */
    @GetMapping(value = "/streaming_ask", produces = "text/stream;charset=utf-8")
    public Flux<String> streamingAsk(String question) {
        return Flux.create(emitter -> {
            qwenStreamingChatModel.generate(question, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    emitter.next(token);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    StreamingResponseHandler.super.onComplete(response);
                }

                @Override
                public void onError(Throwable error) {
                    emitter.error(error);
                }
            });
        });
    }

    /**
     * Question answering using Ollama model
     *
     * @param question the user's question
     * @return AI response as string
     */
    @GetMapping("/ask")
    public String ask(String question) {
        return ollamaChatModel.generate(question);
    }
    
    /**
     * Question answering using Qwen model
     *
     * @param question the user's question
     * @return AI response as string
     */
    @GetMapping("/qwen_ask")
    public String qwenAsk(String question) {
        return qwenChatModel.generate(question);
    }
    
    /**
     * Question answering using FAQ RAG chain
     *
     * @param question the user's question
     * @return AI response from FAQ knowledge base
     */
    @GetMapping("/ask_faq")
    public String askFaq(String question) {
        return faqRagChain.execute(question);
    }

    /**
     * Question answering using knowledge RAG chain
     *
     * @param question the user's question
     * @return AI response from knowledge base
     */
    @GetMapping("/ask_knowledge")
    public String askKnowledge(String question) {
        return knowledgeRagChain.execute(question);
    }

    /**
     * Question answering using message pool RAG chain with cleanup
     *
     * @param question the user's question
     * @return cleaned AI response from message pool
     */
    @GetMapping("/ask_msg")
    public String askMsg(String question) {
        log.info(question);
        String answer = msgPoolRagChain.execute(question);
        answer = answer.replaceAll("(?s)<think>.*?</think>", "").trim();
        log.info(answer);
        return answer;
    }
}
