package com.example.springai.service;

import com.example.springai.config.SimpleEmbeddingConfig;
import com.example.springai.model.DocumentChunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorStoreServiceTest {

    private VectorStoreService vectorStoreService;

    @BeforeEach
    void setUp() {
        EmbeddingClient embeddingClient = new SimpleEmbeddingConfig().embeddingClient();
        SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingClient);
        vectorStoreService = new VectorStoreService(vectorStore);

        DocumentService documentService = new DocumentService();
        List<DocumentChunk> chunks = documentService.loadAndSplitDocuments();
        vectorStoreService.indexChunks(chunks);
    }

    @Test
    void testIndexing() {
        assertTrue(vectorStoreService.getDocumentCount() > 0);
        System.out.println("✅ Проиндексировано: " + vectorStoreService.getDocumentCount());
    }

    @Test
    void testExtractCode() {
        List<String> kws = vectorStoreService.extractKeywords("Кто такой EMP-0010?");
        assertTrue(kws.contains("EMP-0010"), "Должен извлечь EMP-0010");
    }

    @Test
    void testExtractSiteCode() {
        List<String> kws = vectorStoreService.extractKeywords("Кто на SITE-04?");
        assertTrue(kws.contains("SITE-04"));
    }

    @Test
    void testExtractMarker() {
        List<String> kws = vectorStoreService.extractKeywords("У кого маркер VXL2FL3KNH?");
        assertTrue(kws.contains("VXL2FL3KNH"), "Должен извлечь 10-символьный маркер");
    }

    @Test
    void testMorphology() {
        List<String> kws = vectorStoreService.extractKeywords("Какой грейд у Полевца?");
        assertFalse(kws.isEmpty(), "Должен извлечь хотя бы одно слово");
        assertTrue(kws.stream().anyMatch(k -> k.toLowerCase().startsWith("полевц")),
                "Морфология: 'Полевца' → 'Полевц'");
    }

    @Test
    void testKeywordSearch() {
        List<?> docs = vectorStoreService.keywordSearch("EMP-0010", 5);
        assertFalse(docs.isEmpty(), "EMP-0010 должен найтись");
    }

    @Test
    void testEmptyQuery() {
        assertTrue(vectorStoreService.keywordSearch("", 5).isEmpty());
        assertTrue(vectorStoreService.keywordSearch(null, 5).isEmpty());
    }
}
