package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
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
    private MessageService messageService; 
    @Autowired
    private AccountService accountService;

     // Create a new message
     @PostMapping("/messages")
     public ResponseEntity<?> createMessage(@RequestBody Message message) {
         try {
             Message savedMessage = messageService.createMessage(message);
             return ResponseEntity.ok(savedMessage);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }
 
     // Get all messages
     @GetMapping("/messages")
     public ResponseEntity<List<Message>> getAllMessages() {
         return ResponseEntity.ok(messageService.getAllMessages());
     }
 
     // Get message by ID
     @GetMapping("/messages/{messageId}")
     public ResponseEntity<?> getMessageById(@PathVariable int messageId) {
         Optional<Message> message = messageService.getMessageById(messageId);
         return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().build()); // Return 200 with empty body
     }
 
     // Delete message by ID
     @DeleteMapping("/messages/{messageId}")
     public ResponseEntity<?> deleteMessageById(@PathVariable int messageId) {
         try {
             messageService.deleteMessageById(messageId);
             return ResponseEntity.ok(1);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.ok().build(); // Follow test case expectations
         }
     }
 
     // Update a message
     @PatchMapping("/messages/{id}")
     public ResponseEntity<?> updateMessage(@PathVariable int id, @RequestBody Map<String, String> body) {
         try {
             String newText = body.get("messageText");
             int result = messageService.updateMessage(id, newText);
             return ResponseEntity.ok(result);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
     }
 
     // Get messages by account ID
     @GetMapping("/accounts/{id}/messages")
     public ResponseEntity<?> getMessagesByAccountId(@PathVariable int id) {
         try {
             List<Message> messages = messageService.getMessagesByUserId(id);
             return ResponseEntity.ok(messages);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
         }
     }
       // Register a new account
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            Account savedAccount = accountService.register(account);
            return ResponseEntity.ok(savedAccount); // 200 OK with saved account
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Username already exists.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
            }
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request for other validation errors
        }
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account validAccount = accountService.login(account.getUsername(), account.getPassword());
        if (validAccount != null) {
            return ResponseEntity.ok(validAccount); // 200 OK with account object
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"); // 401 Unauthorized
        }
    }
}