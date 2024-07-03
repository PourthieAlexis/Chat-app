package com.example.chat_app.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private String type;

    public Message(String content, Chat chat, User sender, User recipient, String type) {
        this.content = content;
        this.chat = chat;
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
    }

    public Message(String content, User sender, String type) {
        this.content = content;
        this.sender = sender;
        this.type = type;
    }
}
