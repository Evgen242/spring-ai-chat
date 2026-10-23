package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final List<Document> indexedDocuments = new ArrayList<>();

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void indexChunks(List<DocumentChunk> chunks) {
        List<Document> documents = chunks.stream()
            .map(chunk -> new Document(
                chunk.id(),
                chunk.content(),
                Map.of(
                    "source", chunk.source(),
                    "chunkIndex", String.valueOf(chunk.chunkIndex())
                )
            ))
            .toList();

        vectorStore.add(documents);
        indexedDocuments.clear();
        indexedDocuments.addAll(documents);

        System.out.println("✅ Проиндексировано " + documents.size() + " чанков");
    }

    /**
     * Векторный поиск (по эмбеддингам).
     */
    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }

    /**
     * Keyword-поиск (по точному вхождению подстроки).
     * Ищет по ID, именам, кодам — там, где векторный поиск слаб.
     */
    public List<Document> keywordSearch(String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String q = query.toLowerCase().trim();

        return indexedDocuments.stream()
            .filter(doc -> doc.getContent() != null
                    && doc.getContent().toLowerCase().contains(q))
            .limit(limit)
            .toList();
    }

    /**
     * Извлечь ключевые слова из запроса (ID, коды, имена).
     * Ищем паттерны: EMP-1234, SITE-05, DEP-09, PRJ-0261 и т.п.
     */
    public List<String> extractKeywords(String query) {
        List<String> keywords = new ArrayList<>();

        // Паттерны для кодов: EMP-0010, SITE-07, DEP-09, PRJ-0261, PO-02099
        java.util.regex.Pattern codePattern =
            java.util.regex.Pattern.compile("[A-Z]{2,5}-\\d{2,5}");
        java.util.regex.Matcher matcher = codePattern.matcher(query.toUpperCase());

        while (matcher.find()) {
            keywords.add(matcher.group());
        }

        // Поиск маркеров (10 символов A-Z0-9, например VXL2FL3KNH)
        java.util.regex.Pattern markerPattern =
            java.util.regex.Pattern.compile("[A-Z0-9]{10}");
        java.util.regex.Matcher markerMatcher = markerPattern.matcher(query.toUpperCase());
        while (markerMatcher.find()) {
            keywords.add(markerMatcher.group());
        }

        // Если нет кодов — ищем имена (два слова с заглавной буквы)
        if (keywords.isEmpty()) {
            java.util.regex.Pattern namePattern =
                java.util.regex.Pattern.compile("[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+");
            java.util.regex.Matcher nameMatcher = namePattern.matcher(query);
            while (nameMatcher.find()) {
                String fullName = nameMatcher.group();
                keywords.add(fullName);
                // Морфология: добавляем корень имени (без окончания)
                // "Полевца" -> "Полевц", "Яровиной" -> "Яровин"
                String[] parts = fullName.split("\\s+");
                if (parts.length > 0) {
                    String surname = parts[0];
                    if (surname.length() > 4) {
                        String stem = surname.substring(0, surname.length() - 2);
                        if (!stem.isEmpty()) {
                            keywords.add(stem);
                        }
                    }
                }
            }
        }

        // Если имён (два слова) нет — ищем одно слово с заглавной (фамилию)
        if (keywords.isEmpty()) {
            java.util.regex.Pattern singleNamePattern =
                java.util.regex.Pattern.compile("[А-ЯЁ][а-яё]{3,}");
            java.util.regex.Matcher singleNameMatcher = singleNamePattern.matcher(query);
            while (singleNameMatcher.find()) {
                keywords.add(singleNameMatcher.group());
            }
        }

        return keywords;
    }

    public int getDocumentCount() {
        return indexedDocuments.size();
    }
}
