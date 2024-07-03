package com.example.chat_app.service;

import com.example.chat_app.dto.PrivateMessageDTO;
import com.example.chat_app.exception.ChatAlreadyExistsException;
import com.example.chat_app.exception.RecipientNotFoundException;
import com.example.chat_app.exception.SenderNotFoundException;
import com.example.chat_app.dto.ChatDTO;
import com.example.chat_app.dto.MessageDTO;
import com.example.chat_app.model.Chat;
import com.example.chat_app.model.Message;
import com.example.chat_app.model.User;
import com.example.chat_app.repository.ChatRepository;
import com.example.chat_app.repository.MessageRepository;
import com.example.chat_app.repository.UserRepository;


import org.hibernate.Hibernate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {

    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Transactional
    public Chat createChat(Long firstUserId, Long secondUserId) {
        User firstUser = userRepository.findById(firstUserId).orElseThrow(() -> new IllegalArgumentException("User with ID " + firstUserId + " not found"));
        User secondUser = userRepository.findById(secondUserId).orElseThrow(() -> new IllegalArgumentException("User with ID " + secondUserId + " not found"));

        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(firstUser, secondUser);
        if (existingChat.isPresent()) {
            throw new ChatAlreadyExistsException("Chat already exists between the users");
        }

        Chat chat = new Chat(firstUser, secondUser);
        chat = chatRepository.save(chat);
    
        Hibernate.initialize(chat.getFirstUser().getChatsInitiated());
        Hibernate.initialize(chat.getSecondUser().getChatsReceived());
    
        return chat;
    }
    

    @Transactional(readOnly = true)
    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
    }    

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByChatId(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
        Hibernate.initialize(chat.getMessageList());
        return mapMessagesToDTO(chat.getMessageList());
    }
    

    @Transactional(readOnly = true)
    public List<MessageDTO> getPublicMessages() {
        List<Message> publicMessages = messageRepository.findByType("public");
        return mapMessagesToDTO(publicMessages);
    }

    @Transactional(readOnly = true)
    public List<ChatDTO> getChatDTOsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        Hibernate.initialize(user.getChatsInitiated());
        Hibernate.initialize(user.getChatsReceived());
    
        List<Chat> chatsInitiated = user.getChatsInitiated();
        List<Chat> chatsReceived = user.getChatsReceived();
    
        List<Chat> allChats = Stream.concat(chatsInitiated.stream(), chatsReceived.stream())
                .distinct()
                .collect(Collectors.toList());
    
        return allChats.stream().map(this::mapChatToDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public void sendMessage(MessageDTO messageDTO) {
        User sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(() -> new IllegalArgumentException("User with ID " + messageDTO.getSenderId() + " not found"));
        Message message = new Message(messageDTO.getContent(), sender, "public");
        messageRepository.save(message);
    }
    

    @Transactional
    public void sendPrivateMessage(PrivateMessageDTO privateMessageDTO) {
        Chat chat = chatRepository.findById(privateMessageDTO.getChatId()).orElseThrow(() -> new IllegalArgumentException("Chat with ID " + privateMessageDTO.getChatId() + " not found"));
        User sender = userRepository.findById(privateMessageDTO.getSenderId()).orElseThrow(() -> new IllegalArgumentException("User with ID " + privateMessageDTO.getSenderId() + " not found"));
    
        User recipient = chat.getFirstUser().getId().equals(sender.getId())
                ? chat.getSecondUser()
                : chat.getFirstUser();
    
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient not found for chat with ID " + privateMessageDTO.getChatId());
        }
    
        Message privateMessage = new Message(privateMessageDTO.getContent(), chat, sender, recipient, "private");
        messageRepository.save(privateMessage);
    
        messagingTemplate.convertAndSendToUser(chat.getId().toString(), "/queue/private", privateMessageDTO);
    }
    
    
    public ChatDTO mapChatToDTO(Chat chat) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(chat.getId());
        chatDTO.setFirstUserId(chat.getFirstUser().getId());
        chatDTO.setFirstUserUsername(chat.getFirstUser().getUsername());
        chatDTO.setSecondUserId(chat.getSecondUser().getId());
        chatDTO.setSecondUserUsername(chat.getSecondUser().getUsername());
        chatDTO.setMessages(mapMessagesToDTO(chat.getMessageList()));
        return chatDTO;
    }

    public List<MessageDTO> mapMessagesToDTO(List<Message> messages) {
        if (messages == null) {
            throw new IllegalArgumentException("Messages list cannot be null");
        }
    
        return messages.stream()
                .map(message -> {
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());
                    messageDTO.setContent(message.getContent());
    
                    if (message.getSender() == null) {
                        throw new SenderNotFoundException("Sender is null for message with ID: " + message.getId());
                    } else {
                        messageDTO.setSenderId(message.getSender().getId());
                    }
    
                    if (message.getType().equals("public")) {
                        messageDTO.setRecipientId(null);
                    } else if (message.getRecipient() == null) {
                        throw new RecipientNotFoundException("Recipient is null for message with ID: " + message.getId());
                    } else {
                        messageDTO.setRecipientId(message.getRecipient().getId());
                    }
    
                    return messageDTO;
                })
                .collect(Collectors.toList());
    }

}
