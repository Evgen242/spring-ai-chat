package com.example.springai.service;

import com.example.springai.model.DocumentChunk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RAGServiceTest {

    @Test
    void testDocumentLoading() {
        DocumentService service = new DocumentService();
        List<DocumentChunk> chunks = service.loadAndSplitDocuments();

        assertNotNull(chunks);
        assertTrue(chunks.size() > 0, "Документы должны загружаться");
        System.out.println("✅ Загружено чанков: " + chunks.size());
    }

    @Test
    void testFunctionCalling() {
        FunctionCallingService service = new FunctionCallingService();

        String time = service.getCurrentTime();
        assertNotNull(time);
        assertTrue(time.contains("Текущее время"));

        double sum = service.calculateSum(5, 3);
        assertEquals(8.0, sum, 0.001);

        String sysInfo = service.getSystemInfo();
        assertTrue(sysInfo.contains("Spring AI"));
    }
}
