package com.example.springai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiChatResponse(
    @JsonProperty("reply") String reply,
    @JsonProperty("parsedInfo") ParsedResponse parsedInfo
) {}
