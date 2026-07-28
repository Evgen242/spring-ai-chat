# Spring AI Chat Application

REST API application built with Spring Boot and Spring AI for interacting with Large Language Models (LLM) through Ollama.

---

## Overview

This project demonstrates the integration of Spring AI into a Spring Boot application.

The service accepts user requests through a REST API, generates prompts using `PromptTemplate`, sends them to an LLM via `ChatClient`, converts the response into a structured Java object, and returns the result as JSON.

---

## Features

- Spring Boot REST API
- Spring AI integration
- ChatClient API
- PromptTemplate support
- Structured Output mapping
- Ollama integration
- Docker support
- Docker Compose deployment
- VPS deployment

---

## Technology Stack

| Component | Version |
|-----------|----------|
| Java | 17 |
| Spring Boot | 3.2.x |
| Spring AI | 0.8.x |
| Maven | 3.9+ |
| Ollama | Latest |
| Docker | Latest |

---

## Architecture

```
Client
   │
   ▼
ChatController
   │
   ▼
ChatService
   │
   ├── PromptTemplate
   ├── ChatClient
   ▼
Ollama (LLM)
   │
   ▼
ParsedResponse
   │
   ▼
JSON Response
```

---

## Project Structure

```
src
├── main
│   ├── java
│   │   ├── controller
│   │   ├── service
│   │   ├── model
│   │   ├── config
│   │   └── exception
│   └── resources
│       └── application.yml
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Getting Started

### Clone the repository

```bash
git clone https://github.com/Evgen242/spring-ai-chat.git
cd spring-ai-chat
```

### Build

```bash
mvn clean package
```

### Run locally

```bash
mvn spring-boot:run
```

### Run with Docker

```bash
docker compose up -d --build
```

---

## REST API

### Health Check

```
GET /api/health
```

Response

```
OK
```

---

### Chat Endpoint

```
POST /api/chat
Content-Type: application/json
```

Request

```json
{
  "message": "How to create REST API with Spring Boot?"
}
```

Response

```json
{
  "reply": "Summary: ...",
  "parsedInfo": {
    "summary": "...",
    "recommendations": [
      "...",
      "..."
    ],
    "difficulty": "MEDIUM",
    "technologies": [
      "Java",
      "Spring Boot",
      "Spring Web"
    ]
  }
}
```

---

## Example Request

```bash
curl -X POST http://194.154.27.141:8082/api/chat \
-H "Content-Type: application/json" \
-d '{"message":"How to create REST API with Spring Boot?"}'
```

---

## Deployment

The application is deployed on a Linux VPS using Docker Compose.

| Service | URL |
|----------|-----|
| Health Check | http://194.154.27.141:8082/api/health |
| Chat API | http://194.154.27.141:8082/api/chat |

---

## Project Workflow

1. Client sends a POST request.
2. Controller receives the request.
3. Service builds a prompt using `PromptTemplate`.
4. `ChatClient` sends the prompt to Ollama.
5. The model generates a response.
6. The response is mapped to a Java object.
7. The API returns structured JSON.

---

## Requirements Covered

- Spring Boot REST API
- Spring AI integration
- PromptTemplate
- ChatClient
- Structured Output
- Ollama LLM
- Docker containerization
- Docker Compose
- VPS deployment

---

## Author

**Evgen242**

GitHub: https://github.com/Evgen242

---

## License

MIT License
