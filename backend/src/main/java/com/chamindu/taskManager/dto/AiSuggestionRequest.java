package com.chamindu.taskManager.dto;

public class AiSuggestionRequest {

    private String topic;

    public AiSuggestionRequest() {
    }

    public AiSuggestionRequest(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}