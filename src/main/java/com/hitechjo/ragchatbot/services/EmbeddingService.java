package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Embedding> embedChunks(List<TextSegment> segments) {
        return segments.stream()
                .map(segment -> embeddingModel.embed(segment.text()).content())
                .collect(Collectors.toList());
    }

    public Embedding embedQuery(String query) {
        return embeddingModel.embed(query).content();
    }

    @PostConstruct
    public void debugModel() {
        System.out.println("Using embedding model: " + embeddingModel.getClass().getName());
    }
}
