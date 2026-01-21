package com.hitechjo.ragchatbot.controllers;

import com.hitechjo.ragchatbot.dtos.QuestionRequest;
import com.hitechjo.ragchatbot.services.AnswerService;
import com.hitechjo.ragchatbot.services.ChatMemoryService;
import com.hitechjo.ragchatbot.services.PdfIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final AnswerService ragService;
    private final ChatMemoryService chatMemoryService;
    private final PdfIngestionService pdfIngestionService;

    public ChatController(AnswerService ragService, ChatMemoryService chatMemoryService, PdfIngestionService pdfIngestionService) {
        this.ragService = ragService;
        this.chatMemoryService = chatMemoryService;
        this.pdfIngestionService = pdfIngestionService;
    }

    @GetMapping
    public String chatPage() {
        return "chat"; // maps to src/main/resources/templates/chat.html
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            Path tempPath = Files.createTempFile("upload-", ".pdf");
            file.transferTo(tempPath.toFile());

            String result = pdfIngestionService.ingestPdf(tempPath);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam String userId,
                                              @RequestParam String question) {

        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing 'question' in request body.");
        }

        String answer = ragService.answerQuestion(String.valueOf(userId), question);
        return ResponseEntity.ok(answer);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearMemory(@PathVariable String userId) {
        chatMemoryService.clearHistory(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/details")
    public ResponseEntity<Map<String, String>> getUserDetails(@PathVariable String userId) {
        Map<String, String> details = chatMemoryService.getUserDetails(userId);
        return ResponseEntity.ok(details);
    }
}
