package com.hitechjo.ragchatbot.dtos;

public class QuestionRequest {
    private String userId;
    private String question;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "QuestionRequest{" +
                "userId='" + userId + '\'' +
                ", question='" + question + '\'' +
                '}';
    }
}

