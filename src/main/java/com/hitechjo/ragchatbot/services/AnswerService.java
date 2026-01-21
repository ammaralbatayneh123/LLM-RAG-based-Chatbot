package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnswerService {

    private final GoogleAiGeminiChatModel chatModel;
    private final VectorStoreService vectorStoreService;
    private final ChatMemoryService chatMemoryService;
    private final EntityExtractionService entityExtractionService;

    public AnswerService(GoogleAiGeminiChatModel chatModel, VectorStoreService vectorStoreService, ChatMemoryService chatMemoryService, EntityExtractionService entityExtractionService) {
        this.chatModel = chatModel;
        this.vectorStoreService = vectorStoreService;
        this.chatMemoryService = chatMemoryService;
        this.entityExtractionService = entityExtractionService;
    }

    public String answerQuestion(String userId, String question) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        // Step 1: Retrieve top 5 relevant segments based on the question
        List<TextSegment> relevantChunks = vectorStoreService.findRelevantSegments(question, 5);

        // Step 2: Concatenate chunks into a single context string
        StringBuilder contextBuilder = new StringBuilder();
        for (TextSegment chunk : relevantChunks) {
            contextBuilder.append(chunk.text()).append("\n\n");
        }

        // Step 3: Construct the system message (instructions)
        SystemMessage systemMessage = new SystemMessage("""
                You are an intelligent and polite customer support assistant for a software solutions company called "HiTech Jo" based in Jordan.
                
                ### Behavior Rules
                1. On the **first interaction** with a new user:
                   - Greet them warmly (use a friendly tone, feel free to use emojis like 👋).
                   - Politely ask for their **first name** and **last name** before answering any other questions.
                   - Example: "Hello and welcome 👋 Before I can help you, may I kindly know your first and last name?"
                
                2. Once the user's name is known (stored in conversation history):
                   - Always greet them by their first name in your replies.
                   - Example: "Hello Ahmad! How can I help you today?"
                
                3. Answer questions using **only** the provided `Context` and the stored conversation history.
                
                ### Strict Rules
                - Do NOT invent or guess information.
                - If the answer cannot be found in the `Context` or the conversation history, respond exactly with: "I don't know."
                - Keep responses clear, natural, polite, and concise.
                
                ### Provided Context
                Use the following text to answer user questions:
                """);

        // Step 4: Provide the retrieved context as a separate message
        SystemMessage contextMessage = new SystemMessage("Context:\n" + contextBuilder);

        // Step 5: Add user question as a normal message
        UserMessage userPrompt = new UserMessage(question);

        // Step 6: Build the message list: instructions + context + memory + question
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(contextMessage);
        messages.addAll(chatMemoryService.getHistory(userId));  // Memory (name, preferences)
        messages.add(userPrompt);  // New question

        // Step 7: Send the chat request to LLM
        ChatRequest request = ChatRequest.builder().messages(messages).build();

        ChatResponse response = chatModel.chat(request);
        AiMessage aiMessage = response.aiMessage();

        // Step 8: Save this conversation to memory
        chatMemoryService.appendMessage(userId, userPrompt);
        chatMemoryService.appendMessage(userId, aiMessage);

        // Step 9: Extract user details from the message and store (if available)
        Map<String, String> extracted = entityExtractionService.extractUserEntities(aiMessage.text());
        chatMemoryService.storeUserDetails(userId, extracted);
        System.out.println("Stored user entities: " + chatMemoryService.getUserDetails(userId));

        return aiMessage.text();
    }
}
