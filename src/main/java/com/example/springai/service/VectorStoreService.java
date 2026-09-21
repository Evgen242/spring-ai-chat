package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

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
        System.out.println("✅ Проиндексировано " + documents.size() + " чанков");
    }

    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }
}
