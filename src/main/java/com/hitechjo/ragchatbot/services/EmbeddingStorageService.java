package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingStorageService {

    public final EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingStorageService(EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingStore = embeddingStore;
    }

    public void storeEmbeddings(List<TextSegment> segments, List<Embedding> embeddings) {
        for (int i = 0; i < segments.size(); i++) {
            embeddingStore.add(embeddings.get(i), segments.get(i));
        }
    }
}
