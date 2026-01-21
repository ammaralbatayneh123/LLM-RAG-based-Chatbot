package com.hitechjo.ragchatbot.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatRequestParameters;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EntityExtractionService {
    private final GoogleAiGeminiChatModel chatModel;

    public EntityExtractionService(GoogleAiGeminiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public Map<String, String> extractUserEntities(String message) {
        // Step 1: Instruction for LLM to extract structured data
        SystemMessage system = new SystemMessage("""
                    You are an information extractor. Given a user's message, extract any available personal details as JSON.
                
                    Extract the following:
                    - "name": The user's full name.
                    - "email": A valid email address.
                    - "interest": What the user wants to learn (e.g. general English, conversation, TOEFL).
                
                    Return the result strictly as a valid JSON object with keys:
                    {
                      "name": "...",
                      "email": "...",
                      "interest": "..."
                    }
                
                    If a value is not mentioned, set it to null.
                """);

        UserMessage user = new UserMessage(message);

        ChatRequest request = ChatRequest.builder().messages(List.of(system, user))  // ✅ FIX: Correct parameter type
                .build();

        // Step 3: Send the request
        ChatResponse response = chatModel.chat(request);
        AiMessage aiMessage = response.aiMessage();
        String json = aiMessage.text();

        System.out.println("Raw entity extraction output: " + json);

        // Step 4: Parse JSON into a Java Map
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            // Optionally log: e.printStackTrace();
            return Map.of(); // empty result on error
        }
    }
}
