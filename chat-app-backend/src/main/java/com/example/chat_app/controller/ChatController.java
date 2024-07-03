package com.example.chat_app.controller;

import com.example.chat_app.dto.ChatDTO;
import com.example.chat_app.dto.MessageDTO;
import com.example.chat_app.dto.PrivateMessageDTO;
import com.example.chat_app.exception.ChatAlreadyExistsException;
import com.example.chat_app.model.Chat;
import com.example.chat_app.model.User;
import com.example.chat_app.service.ChatService;
import com.example.chat_app.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/chats")
    public ResponseEntity<ChatDTO> createChat(@RequestBody String recipientUsername) {
        String firstUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User firstUser = userService.getUserByUsername(firstUsername);
        User secondUser = userService.getUserByUsername(recipientUsername);

        try {
            Chat chat = chatService.createChat(firstUser.getId(), secondUser.getId());
            return ResponseEntity.ok(chatService.mapChatToDTO(chat));
        } catch (ChatAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChatId(@PathVariable Long chatId) {
        List<MessageDTO> messages = chatService.getMessagesByChatId(chatId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getPublicMessages() {
        List<MessageDTO> publicMessages = chatService.getPublicMessages();
        return new ResponseEntity<>(publicMessages, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/chats")
    public ResponseEntity<List<ChatDTO>> getChats(@PathVariable Long userId) {
        List<ChatDTO> chats = chatService.getChatDTOsByUserId(userId);
        return ResponseEntity.ok(chats);
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public MessageDTO sendMessage(@Payload MessageDTO messageDTO) {
        chatService.sendMessage(messageDTO);
        return messageDTO;
    }

    @MessageMapping("/privateMessage")
    public void sendPrivateMessage(@Payload PrivateMessageDTO privateMessageDTO) {
        chatService.sendPrivateMessage(privateMessageDTO);
    }
}
