package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextChunkerService {

    private final DocumentSplitter splitter = DocumentSplitters.recursive(1000, 150);

    public List<TextSegment> chunk(Document document) {
        return splitter.split(document);
    }
}
