package com.example.springai.service;

import com.example.springai.model.AiChatResponse;
import com.example.springai.model.ParsedResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${openrouter.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    public AiChatResponse processMessage(String userMessage) {
        try {
            String systemPrompt = """
                    Ты - опытный IT-консультант с 10-летним стажем.
                    Твоя задача - помогать пользователям с техническими вопросами.
                    Отвечай структурированно и профессионально на РУССКОМ языке.
                    """;

            String userPromptTemplate = """
                    Вопрос пользователя: {question}
                    
                    Контекст: пользователь интересуется областью {tech_area}
                    
                    Пожалуйста, предоставь структурированный ответ в формате:
                    Summary: краткое резюме на русском
                    Recommendations: список рекомендаций через запятую на русском
                    Difficulty: EASY/MEDIUM/HARD
                    Technologies: список необходимых технологий через запятую
                    
                    Ответ должен быть на РУССКОМ языке.
                    """;

            String promptContent = userPromptTemplate
                .replace("{question}", userMessage)
                .replace("{tech_area}", determineTechArea(userMessage));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://194.154.27.141:8082");
            headers.set("X-Title", "Spring AI Chat Application");

            Map<String, Object> requestBody = Map.of(
                "model", "openrouter/free",
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", promptContent)
                ),
                "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                OPENROUTER_URL,
                entity,
                String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String content = jsonResponse
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();

            ParsedResponse parsed = parseResponse(content);
            return new AiChatResponse(content, parsed);

        } catch (Exception e) {
            e.printStackTrace();
            ParsedResponse fallback = new ParsedResponse(
                "Ошибка: " + e.getMessage(),
                List.of("Проверьте подключение к OpenRouter", "Проверьте API-ключ"),
                "MEDIUM",
                List.of("Spring Boot", "Java")
            );
            return new AiChatResponse("Извините, произошла ошибка: " + e.getMessage(), fallback);
        }
    }

    private String determineTechArea(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("spring") || msg.contains("java")) return "Java/Spring";
        if (msg.contains("python") || msg.contains("django")) return "Python";
        if (msg.contains("react") || msg.contains("angular")) return "Frontend";
        if (msg.contains("docker") || msg.contains("kubernetes")) return "DevOps";
        if (msg.contains("ci/cd") || msg.contains("jenkins")) return "CI/CD";
        return "Общие IT вопросы";
    }

    private ParsedResponse parseResponse(String response) {
        String summary = extractValue(response, "Summary", "резюме");
        List<String> recommendations = extractList(response, "Recommendations", "рекомендац");
        String difficulty = extractValue(response, "Difficulty", "сложност");
        List<String> technologies = extractList(response, "Technologies", "технолог");

        if (summary.isEmpty()) summary = "Анализ запроса выполнен";
        if (recommendations.isEmpty()) recommendations = List.of("Изучите документацию");
        if (difficulty.isEmpty()) difficulty = "MEDIUM";
        if (technologies.isEmpty()) technologies = List.of("Java", "Spring Boot");

        return new ParsedResponse(summary, recommendations, difficulty.toUpperCase(), technologies);
    }

    private String extractValue(String text, String key, String fallbackKey) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains(key.toLowerCase()) || lowerLine.contains(fallbackKey.toLowerCase())) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    return line.substring(colonIndex + 1).trim();
                }
                return line.trim();
            }
        }
        return "";
    }

    private List<String> extractList(String text, String key, String fallbackKey) {
        String value = extractValue(text, key, fallbackKey);
        if (value.isEmpty()) return List.of();
        return Arrays.stream(value.split("[,;]"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
}
