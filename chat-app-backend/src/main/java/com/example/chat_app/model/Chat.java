package com.example.chat_app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.AccessLevel;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_user_id")
    private User secondUser;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    private List<Message> messageList;

    public List<Message> getMessageList() {
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }

     public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }
}
