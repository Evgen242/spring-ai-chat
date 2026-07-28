package com.example.springai.controller;

import com.example.springai.model.ChatRequest;
import com.example.springai.model.AiChatResponse;
import com.example.springai.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody ChatRequest request) {
        if (request == null || request.message() == null || request.message().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Received: " + request.message());
        AiChatResponse response = chatService.processMessage(request.message());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
