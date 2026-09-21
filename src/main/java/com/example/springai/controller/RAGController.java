package com.example.springai.controller;

import com.example.springai.model.RAGRequest;
import com.example.springai.model.RAGResponse;
import com.example.springai.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RAGController {

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/rag")
    public ResponseEntity<RAGResponse> ragChat(@RequestBody RAGRequest request) {
        if (request == null || request.message() == null || request.message().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("RAG Received: " + request.message());
        RAGResponse response = ragService.processQuery(request.message());
        return ResponseEntity.ok(response);
    }
}
