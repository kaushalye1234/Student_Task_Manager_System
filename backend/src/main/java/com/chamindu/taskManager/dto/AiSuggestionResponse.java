package com.chamindu.taskManager.dto;

import java.util.List;

public class AiSuggestionResponse {

    private List<String> suggestions;

    public AiSuggestionResponse() {
    }

    public AiSuggestionResponse(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}