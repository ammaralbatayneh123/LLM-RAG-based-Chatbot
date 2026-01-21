package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorStoreService {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingService embeddingService;

    public VectorStoreService(EmbeddingStore<TextSegment> embeddingStore, EmbeddingService embeddingService) {
        this.embeddingStore = embeddingStore;
        this.embeddingService = embeddingService;
    }

    public List<TextSegment> findRelevantSegments(String query, int topK) {
        Embedding queryEmbedding = embeddingService.embedQuery(query);

        // Correct way to build the EmbeddingSearchRequest using the builder pattern.
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK)
                .build();

        // Perform the search using the request object
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(request);

        // Extract the relevant segments from the search result
        return searchResult.matches()
                .stream()
                .map(EmbeddingMatch::embedded)
                .toList();
    }
}
