package com.lc.lc4jdemo.raq.faq;

import com.lc.lc4jdemo.raq.faq.excel.ExcelLoader;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class FaqRagConfig {
    private final Map<String, ChatMemory> memoryMap = new ConcurrentHashMap<>();

    @Bean
    public ConversationalRetrievalChain faqRagChain(OllamaChatModel ollamaChatModel, OllamaEmbeddingModel ollamaEmbeddingModel,ContentRetriever contentRetriever) throws IOException {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // 9. 构建 ConversationalRetrievalChain
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(ollamaChatModel)
                .chatMemory(chatMemory)
                .contentRetriever(contentRetriever)
                .build();
    }

    public ContentRetriever contentRetriever(OllamaEmbeddingModel ollamaEmbeddingModel) throws IOException {
        // 1. 初始化文档分段列表
        List<TextSegment> segments = new ArrayList<>();

        // 3. 加载 Excel 文件并分段
        String path = this.getClass().getResource("/FAQ_CN.xlsx").getPath();

        File excelFile = new File(path);
        if (excelFile.exists()) {
            // 使用自定义的 ExcelLoader 加载并分段 Excel 文件
            segments.addAll(ExcelLoader.loadExcelAsSegments(excelFile));
        }


        // 5. 创建内存中的嵌入存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 6. 将每个文本分段转换为嵌入并存储
        for (TextSegment segment : segments) {
            embeddingStore.add(ollamaEmbeddingModel.embed(segment.text()).content(), segment);
        }

        // 7. 创建 ContentRetriever，用于从嵌入存储中检索相关内容
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(ollamaEmbeddingModel)
                .maxResults(3) // 设置最大返回结果数为 3
                .build();
        return contentRetriever;
    }
}
