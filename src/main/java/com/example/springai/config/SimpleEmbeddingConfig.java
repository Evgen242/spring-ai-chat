package com.example.springai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SimpleEmbeddingConfig {

    private static final int DIMENSIONS = 512;

    @Bean
    public EmbeddingClient embeddingClient() {
        return new EmbeddingClient() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = new ArrayList<>();
                List<String> texts = request.getInstructions();
                for (int i = 0; i < texts.size(); i++) {
                    List<Double> vector = toDoubleList(textToVector(texts.get(i)));
                    embeddings.add(new Embedding(vector, i));
                }
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public List<Double> embed(Document document) {
                return toDoubleList(textToVector(document.getContent()));
            }
        };
    }

    private List<Double> toDoubleList(float[] vector) {
        List<Double> result = new ArrayList<>(vector.length);
        for (float v : vector) {
            result.add((double) v);
        }
        return result;
    }

    /**
     * Bag-of-words хеш-эмбеддинг.
     * - Каждое слово даёт вклад в вектор (TF).
     * - Хеш слова определяет, в какую размерность писать.
     * - Нормализация L2 — чтобы косинусное сходство работало корректно.
     */
    private float[] textToVector(String text) {
        float[] vector = new float[DIMENSIONS];

        if (text == null || text.isBlank()) {
            return vector;
        }

        // Приводим к нижнему регистру и разбиваем на слова
        String normalized = text.toLowerCase()
                .replaceAll("[^a-zа-яё0-9\\-]", " ")
                .trim();

        String[] words = normalized.split("\\s+");

        // TF: каждое слово увеличивает свою размерность
        for (String word : words) {
            if (word.length() < 2) continue;
            int idx = Math.floorMod(word.hashCode(), DIMENSIONS);
            vector[idx] += 1.0f;
        }

        // Дополнительно — биграммы (пары слов) для контекста
        for (int i = 0; i < words.length - 1; i++) {
            String bigram = words[i] + "_" + words[i + 1];
            int idx = Math.floorMod(bigram.hashCode(), DIMENSIONS);
            vector[idx] += 0.5f;
        }

        // L2-нормализация
        double norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < DIMENSIONS; i++) {
                vector[i] /= (float) norm;
            }
        }

        return vector;
    }
}
