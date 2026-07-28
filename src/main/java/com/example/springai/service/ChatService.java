package com.example.springai.service;

import com.example.springai.model.AiChatResponse;
import com.example.springai.model.ParsedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    public AiChatResponse processMessage(String userMessage) {
        // Системный промпт (роль ассистента)
        String systemPrompt = """
                Ты - опытный IT-консультант с 10-летним стажем.
                Твоя задача - помогать пользователям с техническими вопросами.
                Отвечай структурированно и профессионально.
                """;

        // PromptTemplate с 2+ переменными (эмулируем)
        String promptTemplate = """
                Вопрос пользователя: %s
                Область технологий: %s
                Ответь структурированно.
                """;
        
        String techArea = determineTechArea(userMessage);
        String formattedPrompt = String.format(promptTemplate, userMessage, techArea);
        
        // Формируем ответ (мок)
        String response = String.format("""
                Summary: Для создания REST API на Spring Boot нужно выполнить несколько шагов.
                
                Recommendations: Начните с Spring Initializr, добавьте зависимость spring-boot-starter-web, создайте контроллер с аннотацией @RestController.
                
                Difficulty: MEDIUM
                
                Technologies: Java, Spring Boot, Spring Web, Maven/Gradle
                """, userMessage, techArea);
        
        // Структурированный вывод в Java-объект
        ParsedResponse parsed = parseResponse(response);
        
        return new AiChatResponse(response, parsed);
    }

    private String determineTechArea(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("spring") || msg.contains("java")) {
            return "Java/Spring";
        } else if (msg.contains("python") || msg.contains("django")) {
            return "Python";
        } else if (msg.contains("react") || msg.contains("angular")) {
            return "Frontend";
        } else if (msg.contains("docker") || msg.contains("kubernetes")) {
            return "DevOps";
        }
        return "Общие IT вопросы";
    }

    private ParsedResponse parseResponse(String response) {
        String summary = extractValue(response, "Summary", "резюме");
        List<String> recommendations = extractList(response, "Recommendations", "рекомендац");
        String difficulty = extractValue(response, "Difficulty", "сложност");
        List<String> technologies = extractList(response, "Technologies", "технолог");

        if (summary.isEmpty()) summary = "Анализ запроса выполнен";
        if (recommendations.isEmpty()) recommendations = List.of("Изучите документацию", "Начните с практики");
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
        if (value.isEmpty()) {
            return List.of();
        }
        String[] items = value.split("[,;]");
        return List.of(items);
    }
}
