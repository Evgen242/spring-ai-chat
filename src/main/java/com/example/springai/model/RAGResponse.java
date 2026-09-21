package com.example.springai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RAGResponse(
    @JsonProperty("reply") String reply,
    @JsonProperty("sources") List<DocumentChunk> sources,
    @JsonProperty("model") String model
) {}
