package com.hitechjo.ragchatbot.services;

import dev.langchain4j.data.message.ChatMessage;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatMemoryService {
    private final Map<String, List<ChatMessage>> userHistories = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> userDetails = new ConcurrentHashMap<>();

    public void appendMessage(String userId, ChatMessage message) {
        userHistories.computeIfAbsent(userId, id -> new ArrayList<>()).add(message);
    }

    public List<ChatMessage> getHistory(String userId) {
        return userHistories.getOrDefault(userId, Collections.emptyList());
    }

    public void clearHistory(String userId) {
        userHistories.remove(userId);
        userDetails.remove(userId);
    }

    public void storeUserDetails(String userId, Map<String, String> details) {
        userDetails.merge(userId, details, (oldMap, newMap) -> {
            Map<String, String> merged = new HashMap<>(oldMap);
            newMap.forEach((key, value) -> {
                if (value != null) {
                    merged.put(key, value);
                }
            });
            return merged;
        });
    }

    public Map<String, String> getUserDetails(String userId) {
        return userDetails.getOrDefault(userId, Map.of());
    }

    public void resetHistoryAfter(String userId, Duration duration) {
        // Optional: use a scheduler or timestamped messages for auto-expiry
    }
}
