package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private final MessageRepository messageRepo;

    public MessageService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Optional<Message> createMessage(Message message) {
        if (message.getPostedBy() == null || 
            message.getMessageText() == null || 
            message.getMessageText().isBlank() || 
            message.getMessageText().length() > 255) {
            return Optional.empty();
        }
        return Optional.of(messageRepo.save(message));
    }

    public Optional<String> deleteMessageById(int messageId) {
        if (messageRepo.existsById(messageId)) {
            messageRepo.deleteById(messageId);
            return Optional.of("Message deleted successfully");
        }
        return Optional.empty(); // nothing to delete
    }

    public List<Message> getMessagesByUserId(int userId) {
        return messageRepo.findByPostedBy(userId);
    }
    public int updateMessage(int id, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            throw new IllegalArgumentException("Message text is invalid");
        }

        Optional<Message> optional = messageRepo.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Message message = optional.get();
        message.setMessageText(newText);
        messageRepo.save(message);
        return 1;
    }
}
