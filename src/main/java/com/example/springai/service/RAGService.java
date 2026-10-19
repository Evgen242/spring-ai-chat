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
    private static final int TOP_K = 15;
    private static final String MODEL = "nvidia/nemotron-3.5-lightning:free";

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

            String systemPrompt = String.format("""
                    Ты - RAG-ассистент.

                    ═══════════════════════════════════════════════════════════
                    ВНУТРЕННЯЯ КЛАССИФИКАЦИЯ (НЕ ПОКАЗЫВАЙ ПОЛЬЗОВАТЕЛЮ):
                    ═══════════════════════════════════════════════════════════

                    Шаг 1. Определи тип вопроса МЫСЛЕННО. Не пиши этот шаг в ответе.

                    ТИП 1 — ДЕЙСТВИЕ (время, арифметика, система):
                      - вопрос про время/дату → вызови getCurrentTime
                      - вопрос про арифметику (сложение, вычитание, умножение, деление,
                        знаки + - * / × ÷, слова 'сложи', 'вычти', 'умножь', 'раздели',
                        'посчитай', 'вычисли', 'сколько будет') → вызови calculate
                      - вопрос про систему/стек/приложение/фреймворк/язык программирования → вызови getSystemInfo
                      - НЕ отвечай текстом, а СРАЗУ вызывай функцию!
                      - НЕ выдумывай имена функций, используй ровно: getCurrentTime, calculate, getSystemInfo

                    ТИП 2 — ТЕХНИЧЕСКИЙ ВОПРОС (Spring AI, Docker, RAG, OpenRouter):
                      - отвечай ТОЛЬКО на основе КОНТЕКСТА ниже
                      - если ответа нет в контексте — ответь ровно фразой отказа
                      - НЕ используй свои знания о технологиях

                    ═══════════════════════════════════════════════════════════
                    ФОРМАТ ОТВЕТА (СТРОГО):
                    ═══════════════════════════════════════════════════════════

                    - НЕ пиши «Тип вопроса: …». НЕ пиши «ТИП 1», «ТИП 2».
                    - НЕ пиши «Поскольку…», «Так как…», «Ответ:».
                    - НЕ используй разметку Markdown (никаких **, ##, _, -).
                    - НЕ объясняй, почему ты не можешь ответить.
                    - НЕ извиняйся.

                    Если ответа нет в контексте — ответ должен состоять РОВНО из одной фразы:

                    В предоставленном контексте нет информации по этому вопросу

                    ═══════════════════════════════════════════════════════════
                    КОНТЕКСТ (только для ТИП 2):
                    ═══════════════════════════════════════════════════════════
                    %s
                    """, context.toString());

            String userPrompt = String.format("""
                    Вопрос пользователя: %s

                    Определи тип вопроса МЫСЛЕННО и ответь в заданном формате.
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
                        "description", "Получить текущее время и дату. ОБЯЗАТЕЛЬНО вызывай, " +
                                       "если пользователь спрашивает 'который час', 'сколько времени', " +
                                       "'какое сегодня число', 'какая сегодня дата', 'текущее время', 'текущая дата'",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                    )
                ),
                Map.of(
                    "type", "function",
                    "function", Map.of(
                        "name", "calculate",
                        "description", "Выполняет ЛЮБЫЕ арифметические операции: сложение (+), " +
                                       "вычитание (-), умножение (*), деление (/). " +
                                       "ОБЯЗАТЕЛЬНО вызывай на 'сложи', 'вычти', 'умножь', 'раздели', " +
                                       "'посчитай', 'вычисли', 'сколько будет' с любым из знаков + - * / × ÷. " +
                                       "НЕ пытайся решить пример самостоятельно — всегда вызывай эту функцию.",
                        "parameters", Map.of(
                            "type", "object",
                            "properties", Map.of(
                                "expression", Map.of(
                                    "type", "string",
                                    "description", "Математическое выражение, например '7 * 6' или '5 + 3'"
                                )
                            ),
                            "required", List.of("expression")
                        )
                    )
                ),
                Map.of(
                    "type", "function",
                    "function", Map.of(
                        "name", "getSystemInfo",
                        "description", "Получить информацию о системе, стеке, языке программирования и фреймворке. " +
                                       "ОБЯЗАТЕЛЬНО вызывай на 'какая у тебя система', 'что ты используешь', " +
                                       "'твой стек', 'какой фреймворк', 'какой язык программирования', " +
                                       "'на чём ты написан'",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                    )
                )
            );

            Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
                ),
                "tools", tools,
                "tool_choice", "auto",
                "temperature", 0.0
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

            if (message.has("tool_calls") && message.path("tool_calls").isArray()
                    && message.path("tool_calls").size() > 0) {
                JsonNode toolCall = message.path("tool_calls").path(0);
                String functionName = toolCall.path("function").path("name").asText();
                String arguments = toolCall.path("function").path("arguments").asText();

                // Валидация имени функции: отсекаем галлюцинации вроде "..." или "example_function_name"
                if (functionName == null || !functionName.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    System.out.println("⚠️ Некорректное имя функции: [" + functionName + "]");
                    content = "Не удалось получить ответ. Попробуйте переформулировать вопрос.";
                } else {
                    System.out.println("🔧 Модель вызвала функцию: " + functionName + " args=" + arguments);
                    String functionResult = executeFunction(functionName, arguments);
                    content = "Функция " + functionName + " вернула: " + functionResult;
                }
            }

            // Fallback: если content пустой — возвращаем понятную фразу
            if (content == null || content.isBlank()) {
                System.out.println("⚠️ Пустой ответ от модели");
                content = "Не удалось получить ответ. Попробуйте переформулировать вопрос.";
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

                case "calculate":
                    String expression = args.path("expression").asText();
                    if (expression == null || expression.isBlank()) {
                        return "Ошибка: пустое выражение";
                    }
                    return String.valueOf(evalExpression(expression));

                // Fallback совместимости: если модель вызовет старое имя calculateSum
                case "calculateSum":
                    if (args.has("expression")) {
                        return String.valueOf(evalExpression(args.path("expression").asText()));
                    }
                    double a = args.path("a").asDouble();
                    double b = args.path("b").asDouble();
                    return String.valueOf(functionCallingService.calculateSum(a, b));

                case "getSystemInfo":
                    return functionCallingService.getSystemInfo();

                default:
                    return "Функция не поддерживается: " + name;
            }
        } catch (Exception e) {
            return "Ошибка выполнения функции: " + e.getMessage();
        }
    }

    /**
     * Простой парсер арифметических выражений: "a OP b".
     * Поддерживает +, -, *, /, ×, ÷. Пробелы игнорируются.
     */
    private double evalExpression(String expr) {
        String s = expr.trim()
                .replace("×", "*")
                .replace("÷", "/")
                .replace(" ", "");

        for (String op : new String[]{"+", "-", "*", "/"}) {
            int idx = s.indexOf(op);
            if (idx > 0) {
                double a = Double.parseDouble(s.substring(0, idx));
                double b = Double.parseDouble(s.substring(idx + 1));
                return switch (op) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> a / b;
                    default -> throw new IllegalArgumentException("Неизвестный оператор: " + op);
                };
            }
        }
        throw new IllegalArgumentException("Не могу разобрать выражение: " + expr);
    }
}
