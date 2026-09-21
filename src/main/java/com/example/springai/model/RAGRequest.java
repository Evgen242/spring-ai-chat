package com.example.springai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RAGRequest(
    @JsonProperty("message") String message
) {}
