package com.example.doanmonhoc.model;

public class LoginResponse {
    private String token;
    private String message;

    // Getters và setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
