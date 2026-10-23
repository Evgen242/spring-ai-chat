package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceTest {

    @Test
    void testLoadAndSplit() {
        DocumentService service = new DocumentService();
        List<DocumentChunk> chunks = service.loadAndSplitDocuments();

        assertNotNull(chunks);
        assertFalse(chunks.isEmpty(), "Должны быть чанки");
        assertTrue(chunks.size() > 10, "Чанков должно быть > 10 (не 1)");
        System.out.println("✅ Чанков: " + chunks.size());
    }

    @Test
    void testChunksNotEmpty() {
        DocumentService service = new DocumentService();
        List<DocumentChunk> chunks = service.loadAndSplitDocuments();

        for (DocumentChunk chunk : chunks) {
            assertNotNull(chunk.id());
            assertNotNull(chunk.content());
            assertFalse(chunk.content().isBlank(), "Контент не должен быть пустым");
            assertEquals("sample.txt", chunk.source());
        }
    }

    @Test
    void testNoDuplicateIds() {
        DocumentService service = new DocumentService();
        List<DocumentChunk> chunks = service.loadAndSplitDocuments();
        long uniqueIds = chunks.stream().map(DocumentChunk::id).distinct().count();
        assertEquals(chunks.size(), uniqueIds, "ID должны быть уникальными");
    }
}
