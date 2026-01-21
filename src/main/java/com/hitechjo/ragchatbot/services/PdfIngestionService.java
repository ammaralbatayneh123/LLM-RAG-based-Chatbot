package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class PdfIngestionService {
    private final PDFLoaderService pdfLoader;
    private final TextChunkerService textChunker;
    private final EmbeddingService embeddingService;
    private final EmbeddingStorageService embeddingStorageService;

    public PdfIngestionService(PDFLoaderService pdfLoader, TextChunkerService textChunker, EmbeddingService embeddingService, EmbeddingStorageService embeddingStorageService) {
        this.pdfLoader = pdfLoader;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.embeddingStorageService = embeddingStorageService;
    }

    public String ingestPdf(Path pdfPath) {

        Document doc = pdfLoader.loadPDF(pdfPath);
        List<TextSegment> chunks = textChunker.chunk(doc);
        List<Embedding> embeddings = embeddingService.embedChunks(chunks);

        System.out.println("PDF loaded and chunked into " + chunks.size() + " segments.\n");

        for (int i = 0; i < chunks.size(); i++) {
            TextSegment chunk = chunks.get(i);
            Embedding embedding = embeddings.get(i);
            float[] vector = embedding.vector();

            System.out.println("------------------------------------------------------------");
            System.out.println("Chunk #" + (i + 1));
            System.out.println("Text:");
            System.out.println(chunk.text());
            System.out.println("\nEmbedding (" + vector.length + " dimensions):");

            // Limit printing to first 10 dimensions for brevity
            int previewLength = Math.min(10, vector.length);
            System.out.print("[");
            for (int j = 0; j < previewLength; j++) {
                System.out.printf("%.4f", vector[j]);
                if (j < previewLength - 1) {
                    System.out.print(", ");
                }
            }
            if (vector.length > previewLength) {
                System.out.print(", ...");
            }
            System.out.println("]");
            System.out.println("------------------------------------------------------------\n");
        }

        embeddingStorageService.storeEmbeddings(chunks, embeddings);

        return "Stored " + embeddings.size() + " embeddings to Qdrant.";
    }
}
