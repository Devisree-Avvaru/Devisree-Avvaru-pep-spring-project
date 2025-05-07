package com.example.service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final AccountRepository accountRepo;

    @Autowired
    public MessageService(MessageRepository messageRepo, AccountRepository accountRepo) {
        this.messageRepo = messageRepo;
        this.accountRepo = accountRepo;
    }

    public Message createMessage(Message message) {
        if (message.getPostedBy() == null || 
            message.getMessageText() == null || 
            message.getMessageText().isBlank() || 
            message.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message must not be blank and under 255 characters.");
        }

        if (!accountRepo.existsById(message.getPostedBy())) {
            throw new IllegalArgumentException("Account does not exist.");
        }

        return messageRepo.save(message);
    }

    public void deleteMessageById(int messageId) {
        if (!messageRepo.existsById(messageId)) {
            throw new IllegalArgumentException("Message not found.");
        }
        messageRepo.deleteById(messageId);
    }

    public List<Message> getMessagesByUserId(int userId) {
        if (!accountRepo.existsById(userId)) {
            throw new IllegalArgumentException("Account not found.");
        }
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

    public Optional<Message> getMessageById(int id) {
        return messageRepo.findById(id);
    }

    public List<Message> getAllMessages() {
        return messageRepo.findAll();
    }
}
