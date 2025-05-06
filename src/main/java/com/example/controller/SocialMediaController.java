package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import com.example.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.example.service.AccountService;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class SocialMediaController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageService messageService; 
    @Autowired
    private AccountService accountService;

    // ========== Account Endpoints ==========

    // Create a new account
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username cannot be blank.");
        }
        if (account.getPassword() == null || account.getPassword().length() < 5) {
            return ResponseEntity.badRequest().body("Password must be more than 4 characters.");
        }

        List<Account> existingAccounts = accountRepository.findAll();
        for (Account acc : existingAccounts) {
            if (acc.getUsername().equals(account.getUsername())) {
                return ResponseEntity.badRequest().body("Username must be unique.");
            }
        }

        Account saved = accountRepository.save(account);
        return ResponseEntity.ok(saved);
    }

    // ========== Message Endpoints ==========

    // Create a new message
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return ResponseEntity.badRequest().body("Message must be not blank and under 255 characters.");
        }

        Optional<Account> account = accountRepository.findById(message.getPostedBy());
        if (account.isEmpty()) {
            return ResponseEntity.badRequest().body("Account does not exist.");
        }

        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Get all messages
    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Get message by ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable int messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            return ResponseEntity.ok(message.get()); // Test case 1 expects 200 + body
        } else {
            return ResponseEntity.ok().build(); // Test case 2 expects 200 + empty body
        }
    }

    // Delete message by ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessageById(@PathVariable int messageId) {
        if (!messageRepository.existsById(messageId)) {//check if meesage exist in repository
            return ResponseEntity.ok().build();//return 404 if message not exist
        }
        messageRepository.deleteById(messageId);//delete message by id
        return ResponseEntity.ok(1);
    }

    // Update a message
    @PatchMapping("/messages/{id}")
    public ResponseEntity<Integer> updateMessage(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        try {
            String newText = body.get("messageText");
            int result = messageService.updateMessage(id, newText);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all messages by account ID
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<?> getMessagesByAccountId(@PathVariable int accountId) {
        if (!accountRepository.existsById(accountId)) {
            return ResponseEntity.notFound().build();
        }
        List<Message> messages = messageRepository.findByPostedBy(accountId);
        return ResponseEntity.ok(messages);
    }
   

    // Login user
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Account validAccount = accountService.login(account.getUsername(), account.getPassword());
        if (validAccount != null) {
            return ResponseEntity.ok(validAccount);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username cannot be blank.");
        }

        if (account.getPassword() == null || account.getPassword().length() < 5) {
            return ResponseEntity.badRequest().body("Password must be more than 4 characters.");
        }

        // Check for existing user
        List<Account> existingAccounts = accountRepository.findAll();
        for (Account acc : existingAccounts) {
            if (acc.getUsername().equals(account.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists."); // 409
            }
        }

        Account saved = accountRepository.save(account);
        return ResponseEntity.ok(saved); // 200 OK with user object
    }
}
