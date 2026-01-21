package com.hitechjo.ragchatbot.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Bean
    public GoogleAiGeminiChatModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey("AIzaSyClDr0nZXeCkPLBwCM4j7NoSWrNxBOUViI")
                .modelName("gemini-2.5-flash") // The model for chat interactions
                .temperature(0.7)
                .build();
    }

    /**
     * Configures and provides a GoogleAiEmbeddingModel bean.
     * This bean is used to generate text embeddings for RAG functionality.
     * It uses the "embedding-001" model, which is the recommended model for embeddings.
     * The API key is automatically picked up from the application properties.
     */
    @Bean
    public EmbeddingModel geminiEmbeddingModel() {
        return GoogleAiEmbeddingModel.builder()
                .apiKey("AIzaSyClDr0nZXeCkPLBwCM4j7NoSWrNxBOUViI")
                .modelName("embedding-001") // The model for embedding
                .build();
    }
}
