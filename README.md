# Spring AI Chat Application — Level 3

REST API application built with Spring Boot and Spring AI for interacting with Large Language Models (LLMs) through the OpenRouter API, with Retrieval-Augmented Generation (RAG), vector search, structured responses, and Function Calling support.

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-0.8.x-6DB33F.svg)](https://spring.io/projects/spring-ai)
[![OpenRouter](https://img.shields.io/badge/OpenRouter-API-orange.svg)](https://openrouter.ai/)
[![RAG](https://img.shields.io/badge/RAG-Enabled-purple.svg)](#retrieval-augmented-generation)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Overview

This project demonstrates the integration of Artificial Intelligence capabilities into a Java backend application using Spring Boot and Spring AI.

The application communicates with Large Language Models through the **OpenRouter API** using the `openrouter/free` route. It supports standard AI chat requests, Retrieval-Augmented Generation (RAG), structured output processing, and application-defined functions that can be invoked by the language model.

The project extends the functionality of a basic Spring AI chat application by introducing:

- Retrieval-Augmented Generation (RAG)
- In-memory vector storage
- Custom hash-based embeddings
- Text document processing
- Context-aware AI responses
- Function Calling
- Structured response mapping
- Docker-based deployment
- REST API integration

The application is designed as a practical example of integrating AI functionality into a Spring Boot backend.

---

## Features

- REST API built with Spring Boot
- Spring AI integration
- OpenRouter API integration
- `ChatClient` support
- `PromptTemplate` processing
- Structured Output mapping
- Retrieval-Augmented Generation (RAG)
- In-memory `SimpleVectorStore`
- Custom hash-based embedding implementation
- TXT document loading
- Paragraph-based text splitting
- Context retrieval for AI responses
- Function Calling support
- Current time function
- Mathematical calculation function
- System information function
- JSON request and response processing
- Docker containerization
- Docker Compose deployment
- Linux VPS deployment
- Health check endpoint

---

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.2.x |
| Spring AI | 0.8.x |
| Maven | 3.9+ |
| OpenRouter API | Latest |
| LLM Route | `openrouter/free` |
| Vector Store | `SimpleVectorStore` |
| Embeddings | Custom hash-based implementation |
| Docker | Latest |
| Docker Compose | Latest |
| Deployment | Linux VPS |

---

## LLM Provider

The application integrates with the **OpenRouter API** using the `openrouter/free` route.

The route allows the application to communicate with an available free Large Language Model without binding the implementation to a single model provider.

### Benefits

- Automatic selection of an available free model
- OpenAI-compatible API integration
- Compatibility with Spring AI
- Reduced dependency on a single model provider
- Possibility of switching to commercial models
- Centralized API configuration

The application requires an OpenRouter API key configured through an environment variable.

---

## Architecture

```mermaid
flowchart TD
    Client[Client Application]

    subgraph SpringBoot["Spring Boot Application"]
        Controller[REST Controllers]

        subgraph Chat["Chat Processing"]
            ChatService[Chat Service]
            Prompt[PromptTemplate]
            ChatClient[Spring AI ChatClient]
            Parser[Structured Output Mapping]
        end

        subgraph RAG["RAG Pipeline"]
            Reader[TXT Resource Reader]
            Splitter[Paragraph Text Splitter]
            Embedding[Custom Embedding Service]
            VectorStore[SimpleVectorStore]
            Retrieval[Context Retrieval]
        end

        subgraph Functions["Function Calling"]
            TimeFunction[Get Current Time]
            SumFunction[Calculate Sum]
            SystemFunction[Get System Information]
        end
    end

    subgraph OpenRouter["OpenRouter API"]
        Router[openrouter/free Router]
        LLM[Large Language Model]
    end

    Client --> Controller

    Controller --> ChatService
    Controller --> Retrieval

    ChatService --> Prompt
    Prompt --> ChatClient

    Reader --> Splitter
    Splitter --> Embedding
    Embedding --> VectorStore

    Retrieval --> VectorStore
    VectorStore --> ChatClient

    ChatClient --> OpenRouter
    OpenRouter --> Router
    Router --> LLM

    LLM --> Router
    Router --> OpenRouter
    OpenRouter --> ChatClient

    ChatClient --> Parser
    ChatClient --> TimeFunction
    ChatClient --> SumFunction
    ChatClient --> SystemFunction

    Parser --> Controller
    TimeFunction --> ChatClient
    SumFunction --> ChatClient
    SystemFunction --> ChatClient

    Controller --> Client
````
---

## Project Structure
```
spring-ai-chat/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/
│   │   │   │   ├── ChatController.java
│   │   │   │   └── RAGController.java
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── ChatRequest.java
│   │   │   │   ├── AiChatResponse.java
│   │   │   │   ├── ParsedResponse.java
│   │   │   │   ├── DocumentChunk.java
│   │   │   │   ├── RAGRequest.java
│   │   │   │   └── RAGResponse.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── ChatService.java
│   │   │   │   ├── RAGService.java
│   │   │   │   ├── DocumentService.java
│   │   │   │   ├── VectorStoreService.java
│   │   │   │   └── FunctionCallingService.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── VectorStoreConfig.java
│   │   │   │   └── SimpleEmbeddingConfig.java
│   │   │   │
│   │   │   └── SpringAiApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application-docker.yml
│   │       └── documents/
│   │           └── sample.txt
│   │
│   └── test/
│       └── java/
│           └── com/example/springai/service/
│               └── RAGServiceTest.java
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```
---

# Getting Started

## Prerequisites

Before running the application, make sure the following tools are installed:

- Java 17 or later
- Maven 3.9 or later
- Docker
- Docker Compose
- OpenRouter API key

---

## Clone Repository
```bash
git clone https://github.com/Evgen242/spring-ai-chat.git

cd spring-ai-chat
```
---

## Configure Environment Variables

Set the OpenRouter API key before starting the application.

### Linux
```bash
export OPENROUTER_API_KEY=your_api_key
```
### Windows PowerShell
```powershell
$env:OPENROUTER_API_KEY="your_api_key"
```
The API key should not be committed to the repository.

---

## Build
```bash
mvn clean package
```
To skip tests during packaging:
```bash
mvn clean package -DskipTests
```
---

## Run Locally
```bash
mvn spring-boot:run
```
The application uses the port configured in `application.yml` or through the `SERVER_PORT` environment variable.

---

## Run with Docker

Build and start the application:
```bash
docker compose up -d --build
```
Verify running containers:
```bash
docker ps
```
View application logs:
```bash
docker compose logs -f
```
Stop the application:
```bash
docker compose down
```
---

# REST API

## Health Check
```http
GET /api/health
```
Example request:
```bash
curl http://localhost:8082/api/health
```
Example response:
```
OK
```
The health endpoint can be used to verify that the application is running and accessible.

---

## Chat Endpoint
```http
POST /api/chat
```
Content-Type:
```http
application/json
```
### Request
```json
{
  "message": "How to create REST API with Spring Boot?"
}
```
### Example Request
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How to set up CI/CD?"}' | jq '.'
```
### Example Response
```json
{
  "reply": "Summary: To create a REST API with Spring Boot...",
  "parsedInfo": {
    "summary": "Creating REST API using Spring Boot",
    "recommendations": [
      "Use Spring Initializr",
      "Add Spring Web dependency"
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

## RAG Endpoint
```http
POST /api/rag
```
Content-Type:
```http
application/json
```
### Request
```json
{
  "message": "Что такое RAG?"
}
```
### Example Request
```bash
echo '{"message":"Что такое RAG?"}' > request.json
curl -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d @request.json | jq '.reply'
```
### Example Response
```json
{
  "reply": "RAG (Retrieval-Augmented Generation) — это подход, при котором перед генерацией ответа модель получает релевантные документы из векторного хранилища.",
  "sources": [
    {
      "id": "b864b850-0afb-4dca-8aad-74e04acd73ce",
      "content": "RAG (Retrieval-Augmented Generation) — это подход...",
      "source": "sample.txt",
      "chunkIndex": 1
    }
  ],
  "model": "z-ai/glm-5.2:free"
}
```
The RAG endpoint extends a standard AI request by retrieving relevant information from the local document collection and adding it to the prompt before sending the request to the language model.

### RAG Processing

svg
---

# Retrieval-Augmented Generation

## Overview

Retrieval-Augmented Generation (RAG) is used to provide the language model with additional context retrieved from a local document collection.

Instead of relying only on the information contained in the model, the application performs the following steps:

1. Loads text content from a TXT resource.
2. Splits the document into smaller paragraphs.
3. Generates vector representations using the custom embedding implementation.
4. Stores the document vectors in `SimpleVectorStore`.
5. Searches for relevant content based on the user request.
6. Adds the retrieved context to the AI prompt.
7. Sends the augmented prompt to the language model.
8. Returns the generated response.

---

## RAG Pipeline

svg
---

## Vector Store

The application uses Spring AI's `SimpleVectorStore` as an in-memory vector storage implementation.

The vector store is responsible for storing document content and its corresponding vector representations.

### Characteristics

- In-memory storage
- No external vector database required
- Suitable for demonstration and development
- Simple integration with Spring AI
- Data is not persistent across application restarts unless persistence is explicitly implemented

---

## Custom Embedding Implementation

The project uses a custom hash-based embedding implementation for converting text into numerical vectors.

This approach is intended for demonstrating the RAG pipeline and vector search integration without requiring a separate commercial embedding provider.

The custom embedding implementation should not be considered equivalent to a production-grade semantic embedding model. For production use, a dedicated embedding model may provide more meaningful semantic similarity results.

---

## Document Processing

The RAG pipeline processes text documents through several stages:

| Stage | Description |
| -------------------- | ------------------------------------------------------ |
| Document Loading     | Reads text from a resource file                        |
| Text Splitting       | Divides the document into paragraphs or smaller chunks |
| Embedding            | Converts text into numerical vectors                   |
| Vector Storage       | Stores vectors and associated document content         |
| Similarity Search    | Finds relevant content for a user query                |
| Context Injection    | Adds retrieved content to the AI prompt                |
| Response Generation  | Generates an answer using the LLM                      |

---

# Function Calling

## Overview

Function Calling allows the language model to request the execution of application-defined functions.

The application owns the function implementation and executes the requested operation. The result is then returned to the AI processing flow.

The project includes the following functions:

- `getCurrentTime`
- `calculateSum`
- `getSystemInfo`

---

## Available Functions

| Function | Description | Example Trigger |
| ------------------------------ | ---------------------------------------- | --------------------------------------------- |
| `getCurrentTime`               | Returns the current system time          | "Который час?", "Какое сегодня число?"        |
| `calculateSum`                 | Calculates the sum of two numeric values | "Сколько будет 5 + 3?"                        |
| `getSystemInfo`                | Returns system-related information       | "Какая у тебя система?", "На чём ты написан?" |

### Example
```bash
echo '{"message":"Который час?"}' > request.json
curl -s -X POST http://194.154.27.141:8082/api/rag \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d @request.json | jq '.reply'
```
**Response:**
```
"Функция getCurrentTime вернула: Текущее время: 21.09.2026 11:51:27"
```
---

## Function Calling Flow

svg
---

## Function Calling Responsibilities

The language model:

- Determines whether a function may be useful.
- Selects the appropriate function.
- Provides the required arguments.

The application:

- Registers available functions.
- Validates function arguments.
- Executes the function.
- Returns the function result to the model.
- Controls access to application functionality.

Function execution is performed by the application and should be validated before execution.

---

# AI Response Format

The chat endpoint returns a structured response containing the generated answer and parsed information.

The response may include:

- Summary
- Recommendations
- Difficulty level
- Related technologies

Example:
```json
{
  "summary": "Example summary",
  "recommendations": [
    "Recommendation 1",
    "Recommendation 2"
  ],
  "difficulty": "MEDIUM",
  "technologies": [
    "Java",
    "Spring Boot",
    "Docker"
  ]
}
```
The exact response fields depend on the DTO and structured output configuration used by the application.

---

# Request Processing Flow

## Standard Chat Flow

svg
---

# Configuration

## Environment Variables

| Variable | Description | Required |     |
| ------------------------------- | --------------------------------- | --- |
| `OPENROUTER_API_KEY`            | OpenRouter API key for LLM access | Yes |
| `SERVER_PORT`                   | Application port                  | No  |

The default application port is determined by the project configuration. The deployment examples use port `8082`.

---

## Example Configuration
```yaml
openrouter:
  api-key: ${OPENROUTER_API_KEY}

spring:
  ai:
    openai:
      api-key: ${OPENROUTER_API_KEY}
      base-url: https://openrouter.ai/api/v1
      chat:
        options:
          model: openrouter/free
          temperature: 0.7

server:
  port: ${SERVER_PORT:8080}
```
> The exact configuration keys must match the Spring AI version and the project's current `application-docker.yml` implementation.

---

# Testing

The application includes automated tests and functional validation of the AI REST API.

## JUnit Tests (RAG & Function Calling)

| Metric | Result |
| ---------------- | -------- |
| Test Cases       | 2        |
| Passed           | 2        |
| Failed           | 0        |
| Success Rate     | **100%** |

**Tests:**

- `testDocumentLoading()` — verifies document loading and chunk splitting
- `testFunctionCalling()` — verifies `getCurrentTime`, `calculateSum`, `getSystemInfo`

**Run tests:**
```bash
docker run --rm -v $(pwd):/app -w /app maven:3.9.6-eclipse-temurin-17-alpine mvn test
```
**Output:**
```
[INFO] Running com.example.springai.service.RAGServiceTest
✅ Загружено 10 чанков из sample.txt
✅ Загружено чанков: 10
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
## Functional Test Suite (Simple Chat)

| **MetricResult**      |          |
| --------------------- | -------- |
| Functional Test Cases | 15       |
| Passed                | 15       |
| Failed                | 0        |
| Success Rate          | **100%** |

The test suite was used to validate the application's response processing across different technical topics.

Validated scenarios include:

- Spring Boot REST API
- Docker
- Python
- CI/CD
- Microservices
- Kubernetes
- Git
- Database selection
- REST API security
- GraphQL vs REST
- Unit testing
- Caching
- Asynchronous programming
- Cloud platforms
- Java interview preparation

Each successful response was expected to contain structured information, including:

- Summary
- Recommendations
- Difficulty
- Technologies

---

# Project Requirements

| Requirement | Status |
| ------------------------------- | ----------- |
| Spring Boot REST API            | Implemented |
| Chat endpoint                   | Implemented |
| RAG endpoint                    | Implemented |
| PromptTemplate support          | Implemented |
| Structured Output mapping       | Implemented |
| OpenRouter API integration      | Implemented |
| SimpleVectorStore integration   | Implemented |
| Custom embedding implementation | Implemented |
| TXT document processing         | Implemented |
| Function Calling                | Implemented |
| JUnit tests                     | Implemented |
| Docker containerization         | Implemented |
| Linux VPS deployment            | Implemented |
| Health check endpoint           | Implemented |
| GitHub repository               | Available   |

---

# Deployment

The application is deployed on a Linux VPS using Docker Compose.

## Live Demo

- **Health Check:** [http://194.154.27.141:8082/api/health](http://194.154.27.141:8082/api/health)
- **Chat Endpoint:** `POST http://194.154.27.141:8082/api/chat`
- **RAG Endpoint:** `POST http://194.154.27.141:8082/api/rag`

## Deployment Includes

- Docker Compose orchestration
- Containerized Spring Boot application
- Environment-based configuration
- OpenRouter API integration
- Health check endpoint
- RAG processing
- Function Calling
- Application logs
- Remote Linux deployment

---

## Production Considerations

The current implementation is suitable for learning, demonstration, and further development.

Before using the application in a production environment, consider implementing:

- Secure secret management
- Authentication and authorization
- Request rate limiting
- Input validation
- Error handling
- Persistent vector storage
- Monitoring and alerting
- Centralized logging
- API documentation
- Automated CI/CD pipelines
- Model availability fallback handling

---

# Build Requirements

- Java 17+
- Maven 3.9+
- Docker
- Docker Compose
- OpenRouter API key
- Linux VPS for remote deployment

---

# Future Improvements

- Streaming responses (Flux)
- User authentication and authorization
- Conversation history
- Persistent chat memory
- PostgreSQL integration
- Persistent vector database
- Improved semantic embeddings
- Support for additional document formats
- File upload endpoint for RAG documents
- Multiple LLM providers
- Swagger / OpenAPI documentation
- Extended unit and integration tests
- GitHub Actions CI/CD
- Kubernetes deployment
- Application metrics
- Centralized logging
- Advanced error handling
- Request rate limiting
- Improved RAG evaluation

---

# Learning Objectives

This project demonstrates the following development concepts:

- Building REST APIs with Spring Boot
- Integrating Spring AI into Java applications
- Working with LLM providers through OpenRouter
- Creating prompts with `PromptTemplate`
- Mapping AI responses to Java objects
- Implementing a basic RAG pipeline
- Working with vector storage
- Creating custom embeddings
- Processing text documents
- Implementing Function Calling
- Writing JUnit tests for AI logic
- Containerizing Java applications
- Deploying applications to a Linux VPS
- Validating AI functionality through functional tests

---

# Author

**Evgen242**

GitHub: https://github.com/Evgen242/spring-ai-chat

---

# License

This project is licensed under the MIT License.
