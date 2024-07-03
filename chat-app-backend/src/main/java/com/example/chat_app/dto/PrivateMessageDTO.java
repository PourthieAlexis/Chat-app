package com.example.chat_app.dto;

import lombok.Data;

@Data
public class PrivateMessageDTO {
    private Long chatId;
    private String content;
    private Long senderId;
    private Long recipientId;
}
