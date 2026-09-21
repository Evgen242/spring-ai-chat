package com.example.springai.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FunctionCallingService {

    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "Текущее время: " + now.format(formatter);
    }

    public double calculateSum(double a, double b) {
        return a + b;
    }

    public String getSystemInfo() {
        return "Spring AI Chat Application | Java 17 | Spring Boot 3.2.x | OpenRouter";
    }
}
