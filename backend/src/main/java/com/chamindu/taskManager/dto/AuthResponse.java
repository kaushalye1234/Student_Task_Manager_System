package com.chamindu.taskManager.dto;

public class AuthResponse {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String fullName, String email, String message, String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
