package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    public List<DocumentChunk> loadAndSplitDocuments() {
        List<DocumentChunk> chunks = new ArrayList<>();

        try {
            ClassPathResource resource = new ClassPathResource("documents/sample.txt");
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String[] paragraphs = content.split("\n\n");
            int index = 0;
            for (String paragraph : paragraphs) {
                String trimmed = paragraph.trim();
                if (!trimmed.isEmpty()) {
                    chunks.add(new DocumentChunk(
                        UUID.randomUUID().toString(),
                        trimmed,
                        "sample.txt",
                        index++
                    ));
                }
            }

            System.out.println("✅ Загружено " + chunks.size() + " чанков из sample.txt");

        } catch (IOException e) {
            System.err.println("❌ Ошибка загрузки документа: " + e.getMessage());
            e.printStackTrace();
        }

        return chunks;
    }
}
