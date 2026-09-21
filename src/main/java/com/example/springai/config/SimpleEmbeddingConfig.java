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

    private float[] textToVector(String text) {
        float[] vector = new float[384];
        int hash = text.hashCode();
        for (int i = 0; i < 384; i++) {
            vector[i] = (float) Math.sin(hash * (i + 1) * 0.01);
        }
        return vector;
    }
}
