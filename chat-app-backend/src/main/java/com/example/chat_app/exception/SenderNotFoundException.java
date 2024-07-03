package com.example.chat_app.exception;

public class SenderNotFoundException extends RuntimeException {
    public SenderNotFoundException(String message) {
        super(message);
    }
}