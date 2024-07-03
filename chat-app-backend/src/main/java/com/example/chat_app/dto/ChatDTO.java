package com.example.chat_app.dto;

import java.util.List;
import lombok.Data;

@Data
public class ChatDTO {
    private Long id;
    private Long firstUserId;
    private String firstUserUsername;
    private Long secondUserId;
    private String secondUserUsername;
    private List<MessageDTO> messages;
}
