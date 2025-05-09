package com.lc.lc4jdemo.raq.knowledge;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class KnowledgeRagConfig {
    @Value("${pdf.path:/Users/bing/Downloads/JAVA核心知识点整理1.pdf}")
    private String pdfPath;
    @Bean
    public ConversationalRetrievalChain knowledgeRagChain(OllamaChatModel ollamaChatModel, OllamaEmbeddingModel ollamaEmbeddingModel) throws IOException {
        // 1. 初始化文档分段列表
        List<TextSegment> segments = new ArrayList<>();
        // 2. 加载 PDF 文件并分段
        File pdfFile = new File(pdfPath);
        if (pdfFile.exists()) {
            /**
             * ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
             * Document pdfDocument = parser.parse(new File("docs/internal-doc.pdf"));
             */
            Document document = FileSystemDocumentLoader.loadDocument(pdfFile.toPath());
            // 使用递归切分器，设置最大500字符、重叠50字符
            DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
            segments.addAll(splitter.split(document));

        }

        // 5. 创建内存中的嵌入存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 6. 将每个文本分段转换为嵌入并存储
        for (TextSegment segment : segments) {
            embeddingStore.add(ollamaEmbeddingModel.embed(segment.text()).content(), segment);
        }

        // 7. 创建 ContentRetriever，用于从嵌入存储中检索相关内容
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore).embeddingModel(ollamaEmbeddingModel).maxResults(3) // 设置最大返回结果数为 3
                .build();
        // 8. 构建 ChatMemory (可选，聊天记忆，保留上下文)
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 9. 构建 ConversationalRetrievalChain
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(ollamaChatModel)
                .chatMemory(chatMemory)
                .contentRetriever(contentRetriever)
                .build();
    }
}
