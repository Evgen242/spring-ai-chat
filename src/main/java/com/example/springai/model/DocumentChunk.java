package com.example.springai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DocumentChunk(
    @JsonProperty("id") String id,
    @JsonProperty("content") String content,
    @JsonProperty("source") String source,
    @JsonProperty("chunkIndex") int chunkIndex
) {}
