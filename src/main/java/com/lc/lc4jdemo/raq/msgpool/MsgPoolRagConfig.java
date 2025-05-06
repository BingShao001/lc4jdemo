package com.lc.lc4jdemo.raq.msgpool;

import com.lc.lc4jdemo.chain.MarkdownJsonCleanConversationalChain;
import com.lc.lc4jdemo.chain.ThinkCleanedConversationalChain;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MsgPoolRagConfig {
    @Bean
    public MarkdownJsonCleanConversationalChain msgPoolRagChain(OllamaChatModel ollamaChatModel, OllamaEmbeddingModel ollamaEmbeddingModel, DynamicPromptTemplateProvider dynamicPromptTemplateProvider) throws IOException {
        // 1. 初始化文档分段列表
        List<TextSegment> segments = new ArrayList<>();

        // 3. 加载 Excel 文件并分段
        String path = this.getClass().getResource("/msg_pool.xlsx").getPath();

        File excelFile = new File(path);
        if (excelFile.exists()) {
            // 使用自定义的 ExcelLoader 加载并分段 Excel 文件
            segments.addAll(ExcelLoader.loadExcelAsSegments(excelFile));
        }
        // 4. 创建内存中的嵌入存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 5. 将每个文本分段转换为嵌入并存储
        for (TextSegment segment : segments) {
            embeddingStore.add(ollamaEmbeddingModel.embed(segment.text()).content(), segment);
        }

        // 6. 创建 ContentRetriever，用于从嵌入存储中检索相关内容
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(ollamaEmbeddingModel)
                .maxResults(15) // 设置最大返回结果数为 10
                .minScore(0.5) //最小匹配分数据
                .build();
        // 7. 构建 RetrievalAugmentor + ContentInjector

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(new DynamicContentInjector(dynamicPromptTemplateProvider))
                .build();

        // 8. 构建 ChatMemory (可选，聊天记忆，保留上下文)
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 9. 构建 MarkdownJsonCleanConversationalChain
        ConversationalRetrievalChain conversationalRetrievalChain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(ollamaChatModel)
                .contentRetriever(contentRetriever)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();
        return MarkdownJsonCleanConversationalChain.chain(
                ThinkCleanedConversationalChain.chain(conversationalRetrievalChain));

    }
}
