package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PDFLoaderService {

    private final ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();

    public Document loadPDF(Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return parser.parse(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse PDF: " + path, e);
        }
    }
}
