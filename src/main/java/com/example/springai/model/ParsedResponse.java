package com.example.springai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ParsedResponse(
    @JsonProperty("summary") String summary,
    @JsonProperty("recommendations") List<String> recommendations,
    @JsonProperty("difficulty") String difficulty,
    @JsonProperty("technologies") List<String> technologies
) {}
