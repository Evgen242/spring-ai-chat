package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import com.example.springai.model.RAGResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RAGService {

    @Value("${openrouter.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VectorStoreService vectorStoreService;
    private final DocumentService documentService;
    private final FunctionCallingService functionCallingService;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final int TOP_K = 7;

    public RAGService(VectorStoreService vectorStoreService,
                      DocumentService documentService,
                      FunctionCallingService functionCallingService) {
        this.vectorStoreService = vectorStoreService;
        this.documentService = documentService;
        this.functionCallingService = functionCallingService;

        try {
            List<DocumentChunk> chunks = documentService.loadAndSplitDocuments();
            vectorStoreService.indexChunks(chunks);
        } catch (Exception e) {
            System.err.println("⚠️ Не удалось проиндексировать документы: " + e.getMessage());
        }
    }

    public RAGResponse processQuery(String userQuery) {
        try {
            List<Document> relevantDocs = vectorStoreService.searchSimilar(userQuery, TOP_K);

            StringBuilder context = new StringBuilder();
            for (Document doc : relevantDocs) {
                context.append("- ").append(doc.getContent()).append("\n\n");
            }

            // 👈 ЧЁТКОЕ РАЗДЕЛЕНИЕ ПРАВИЛ
            String systemPrompt = String.format("""
                    Ты - RAG-ассистент с доступом к функциям.
                    
                    ВАЖНО: Определи тип вопроса:
                    
                    ТИП 1 — ДЕЙСТВИЕ (время, сумма, система):
                    - Если вопрос про время/дату → ВЫЗОВИ getCurrentTime
                    - Если вопрос про сумму чисел → ВЫЗОВИ calculateSum
                    - Если вопрос про систему/стек/приложение → ВЫЗОВИ getSystemInfo
                    - НЕ отвечай текстом, а СРАЗУ вызывай функцию!
                    
                    ТИП 2 — ТЕХНИЧЕСКИЙ ВОПРОС (Spring AI, Docker, RAG, OpenRouter):
                    - Отвечай ТОЛЬКО на основе КОНТЕКСТА ниже
                    - Если ответа нет в контексте — скажи "В предоставленном контексте нет информации по этому вопросу"
                    - НЕ используй свои знания о технологиях
                    
                    КОНТЕКСТ (только для ТИП 2):
                    %s
                    """, context.toString());

            String userPrompt = String.format("""
                    Вопрос пользователя: %s

                    Определи тип вопроса и ответь соответствующим образом.
                    """, userQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://194.154.27.141:8082");
            headers.set("X-Title", "Spring AI RAG Application");

            List<Map<String, Object>> tools = List.of(
                Map.of(
                    "type", "function",
                    "function", Map.of(
                        "name", "getCurrentTime",
                        "description", "Получить текущее время и дату. ОБЯЗАТЕЛЬНО вызывай, если пользователь спрашивает 'который час', 'какое сегодня число', 'текущее время'",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                    )
                ),
                Map.of(
                    "type", "function",
                    "function", Map.of(
                        "name", "calculateSum",
                        "description", "Вычислить сумму двух чисел. ОБЯЗАТЕЛЬНО вызывай, если пользователь просит 'сложи', 'сколько будет', 'сумма чисел'",
                        "parameters", Map.of(
                            "type", "object",
                            "properties", Map.of(
                                "a", Map.of("type", "number", "description", "Первое число"),
                                "b", Map.of("type", "number", "description", "Второе число")
                            ),
                            "required", List.of("a", "b")
                        )
                    )
                ),
                Map.of(
                    "type", "function",
                    "function", Map.of(
                        "name", "getSystemInfo",
                        "description", "Получить информацию о системе: приложение, язык, фреймворк, версия. ОБЯЗАТЕЛЬНО вызывай, если пользователь спрашивает 'какая у тебя система', 'что ты используешь', 'твой стек'",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                    )
                )
            );

            Map<String, Object> requestBody = Map.of(
                "model", "openrouter/free",
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
                ),
                "tools", tools,
                "tool_choice", "auto",
                "temperature", 0.1
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                OPENROUTER_URL,
                entity,
                String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode message = jsonResponse.path("choices").path(0).path("message");

            String content = message.path("content").asText();
            String model = jsonResponse.path("model").asText("unknown");

            if (message.has("tool_calls") && message.path("tool_calls").isArray()) {
                JsonNode toolCall = message.path("tool_calls").path(0);
                String functionName = toolCall.path("function").path("name").asText();
                String arguments = toolCall.path("function").path("arguments").asText();

                System.out.println("🔧 Модель вызвала функцию: " + functionName);

                String functionResult = executeFunction(functionName, arguments);
                content = "Функция " + functionName + " вернула: " + functionResult;
            }

            List<DocumentChunk> sources = relevantDocs.stream()
                .map(doc -> new DocumentChunk(
                    doc.getId(),
                    doc.getContent(),
                    (String) doc.getMetadata().getOrDefault("source", "unknown"),
                    Integer.parseInt((String) doc.getMetadata().getOrDefault("chunkIndex", "0"))
                ))
                .toList();

            return new RAGResponse(content, sources, model);

        } catch (Exception e) {
            e.printStackTrace();
            return new RAGResponse("Ошибка: " + e.getMessage(), List.of(), "error");
        }
    }

    private String executeFunction(String name, String arguments) {
        try {
            JsonNode args = objectMapper.readTree(arguments);

            switch (name) {
                case "getCurrentTime":
                    return functionCallingService.getCurrentTime();
                case "calculateSum":
                    double a = args.path("a").asDouble();
                    double b = args.path("b").asDouble();
                    return String.valueOf(functionCallingService.calculateSum(a, b));
                case "getSystemInfo":
                    return functionCallingService.getSystemInfo();
                default:
                    return "Неизвестная функция: " + name;
            }
        } catch (Exception e) {
            return "Ошибка выполнения функции: " + e.getMessage();
        }
    }
}
