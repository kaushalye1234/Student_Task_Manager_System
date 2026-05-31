package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.dto.AiSuggestionRequest;
import com.chamindu.taskManager.dto.AiSuggestionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    @PostMapping("/suggest-tasks")
    public AiSuggestionResponse suggestTasks(@RequestBody AiSuggestionRequest request) {
        String topic = request.getTopic();

        if (topic == null || topic.trim().isEmpty()) {
            return new AiSuggestionResponse(List.of(
                    "Choose a topic you want to study",
                    "Break the topic into small parts",
                    "Create short notes",
                    "Practice example questions",
                    "Review your mistakes"
            ));
        }

        String lowerTopic = topic.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        if (lowerTopic.contains("react") || lowerTopic.contains("frontend")) {
            suggestions.add("Revise React components, props, and state");
            suggestions.add("Practice useState and useEffect examples");
            suggestions.add("Build a small form and handle user input");
            suggestions.add("Connect React frontend with a REST API");
            suggestions.add("Review React Router and component structure");
        } else if (lowerTopic.contains("spring") || lowerTopic.contains("backend") || lowerTopic.contains("java")) {
            suggestions.add("Revise Java OOP concepts");
            suggestions.add("Practice Spring Boot REST controller examples");
            suggestions.add("Create CRUD APIs using Spring Data JPA");
            suggestions.add("Test backend endpoints using Postman");
            suggestions.add("Review MySQL database relationships");
        } else if (lowerTopic.contains("database") || lowerTopic.contains("mysql") || lowerTopic.contains("sql")) {
            suggestions.add("Revise SQL SELECT, INSERT, UPDATE, and DELETE queries");
            suggestions.add("Practice JOIN queries with two or more tables");
            suggestions.add("Review primary keys and foreign keys");
            suggestions.add("Practice database normalization basics");
            suggestions.add("Create sample tables and test queries in MySQL Workbench");
        } else if (lowerTopic.contains("os") || lowerTopic.contains("operating system") || lowerTopic.contains("ossa")) {
            suggestions.add("Revise process states and process control block");
            suggestions.add("Practice CPU scheduling examples");
            suggestions.add("Review deadlock detection and safe sequence problems");
            suggestions.add("Study memory management and paging");
            suggestions.add("Practice Linux commands and shell scripting basics");
        } else if (lowerTopic.contains("interview") || lowerTopic.contains("intern")) {
            suggestions.add("Prepare a short self-introduction");
            suggestions.add("Review your GitHub projects and be ready to explain them");
            suggestions.add("Practice Java, SQL, and React basic questions");
            suggestions.add("Prepare answers for teamwork and problem-solving questions");
            suggestions.add("Update your CV and LinkedIn profile");
        } else {
            suggestions.add("Break down " + topic + " into small study sections");
            suggestions.add("Create short notes for the most important concepts");
            suggestions.add("Practice at least 5 examples or coding questions");
            suggestions.add("Make a quick revision checklist");
            suggestions.add("Test yourself using MCQs or short questions");
        }

        return new AiSuggestionResponse(suggestions);
    }
}