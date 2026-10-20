# Spring AI Chat Application — Level 3

REST API application built with Spring Boot and Spring AI for interacting with Large Language Models (LLMs) through the OpenRouter API. The application includes Retrieval-Augmented Generation (RAG), vector search, structured AI responses, and Function Calling.

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-0.8.x-6DB33F.svg)](https://spring.io/projects/spring-ai)
[![OpenRouter](https://img.shields.io/badge/OpenRouter-API-orange.svg)](https://openrouter.ai/)
[![RAG](https://img.shields.io/badge/RAG-Enabled-purple.svg)](#retrieval-augmented-generation)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Overview

Spring AI Chat Application demonstrates how to integrate Large Language Models into a Java backend application using Spring Boot and Spring AI.

The application communicates with LLMs through the OpenRouter API. The configured model is `nvidia/nemotron-3.5-lightning:free`, providing an explicit model selection for development and testing.

The project extends a basic AI chat application with document retrieval, vector search, structured responses, and application-defined functions.

### Key Features

- REST API built with Spring Boot.
- LLM integration through OpenRouter.
- Spring AI `ChatClient` and `PromptTemplate` support.
- Structured AI response mapping to Java objects.
- Retrieval-Augmented Generation (RAG).
- In-memory vector storage using `SimpleVectorStore`.
- Custom hash-based embedding implementation.
- TXT document loading and paragraph-based splitting.
- Context retrieval for document-grounded responses.
- Function Calling for application-defined operations.
- JSON request and response processing.
- Docker and Docker Compose deployment.
- Linux VPS deployment.
- Health check endpoint.
- Automated unit tests and functional validation.

## Technology Stack

| Component | Technology / Version |
|---|---|
| Programming Language | Java 17 |
| Backend Framework | Spring Boot 3.2.x |
| AI Framework | Spring AI 0.8.x |
| Build Tool | Maven 3.9+ |
| LLM Provider | OpenRouter API |
| LLM Model | `nvidia/nemotron-3.5-lightning:free` |
| Vector Store | Spring AI `SimpleVectorStore` |
| Embeddings | Custom hash-based implementation |
| API Format | REST / JSON |
| Containerization | Docker |
| Container Orchestration | Docker Compose |
| Deployment | Linux VPS |

## Architecture

The application exposes REST endpoints that delegate request processing to the appropriate service. Standard chat requests use the AI chat service, while RAG requests additionally retrieve relevant information from the local document collection.

```mermaid
flowchart TD
    Client[REST API Client]
    API[Spring Boot REST API]
    ChatController[ChatController]
    RAGController[RAGController]
    ChatService[ChatService]
    RAGService[RAGService]
    Functions[Function Calling Service]
    VectorStore[SimpleVectorStore]
    Documents[Local TXT Documents]
    OpenRouter[OpenRouter API]
    Response[JSON Response]

    Client --> API
    API --> ChatController
    API --> RAGController

    ChatController --> ChatService
    ChatService --> OpenRouter
    ChatService --> Functions

    RAGController --> RAGService
    Documents --> VectorStore
    RAGService --> VectorStore
    RAGService --> OpenRouter

    ChatService --> Response
    RAGService --> Response
    Response --> Client
```

### Request Processing

1. The client sends a JSON request to a REST endpoint.
2. The appropriate controller forwards the request to the corresponding service.
3. The service prepares the prompt and processes the request.
4. For RAG requests, relevant document chunks are retrieved from the vector store.
5. The application communicates with the configured language model through OpenRouter.
6. The generated result is returned to the client as a JSON response.

## Project Structure

The following Mermaid diagram represents the directory and file hierarchy of the project.

```mermaid
flowchart TD
    Root["spring-ai-chat/"]

    Root --> Src["src/"]
    Root --> Dockerfile["Dockerfile"]
    Root --> Compose["docker-compose.yml"]
    Root --> Pom["pom.xml"]
    Root --> Readme["README.md"]

    Src --> Main["main/"]
    Src --> Test["test/"]

    Main --> Java["java/"]
    Main --> Resources["resources/"]

    Java --> Controller["controller/"]
    Java --> Model["model/"]
    Java --> Service["service/"]
    Java --> Config["config/"]
    Java --> App["SpringAiApplication.java"]

    Controller --> ChatController["ChatController.java"]
    Controller --> RAGController["RAGController.java"]

    Model --> ChatRequest["ChatRequest.java"]
    Model --> AiChatResponse["AiChatResponse.java"]
    Model --> ParsedResponse["ParsedResponse.java"]
    Model --> DocumentChunk["DocumentChunk.java"]
    Model --> RAGRequest["RAGRequest.java"]
    Model --> RAGResponse["RAGResponse.java"]

    Service --> ChatService["ChatService.java"]
    Service --> RAGService["RAGService.java"]
    Service --> DocumentService["DocumentService.java"]
    Service --> VectorStoreService["VectorStoreService.java"]
    Service --> FunctionCallingService["FunctionCallingService.java"]

    Config --> VectorStoreConfig["VectorStoreConfig.java"]
    Config --> EmbeddingConfig["SimpleEmbeddingConfig.java"]

    Resources --> DockerConfig["application-docker.yml"]
    Resources --> Documents["documents/"]
    Documents --> Sample["sample.txt"]

    Test --> TestJava["java/"]
    TestJava --> TestPackage["com/example/springai/service/"]
    TestPackage --> RAGTest["RAGServiceTest.java"]
```

## Getting Started

### Prerequisites

Before running the application, make sure the following tools are installed:

- Java 17 or later.
- Maven 3.9 or later.
- Docker.
- Docker Compose.
- An OpenRouter API key.

### Clone Repository

```bash
git clone https://github.com/Evgen242/spring-ai-chat.git
cd spring-ai-chat
```

### Configure Environment Variables

The application requires an OpenRouter API key to communicate with the language model.

**Linux / macOS**

```bash
export OPENROUTER_API_KEY="your_api_key"
```

**Windows PowerShell**

```powershell
$env:OPENROUTER_API_KEY="your_api_key"
```

Replace `your_api_key` with your actual OpenRouter API key. Never commit API keys or other credentials to the repository.

### Build the Application

```bash
mvn clean package
```

To package the application without running tests:

```bash
mvn clean package -DskipTests
```

### Run Locally

```bash
mvn spring-boot:run
```

The application port is determined by the project configuration and can be overridden with the `SERVER_PORT` environment variable where supported. The Docker examples use port `8082` on the host and port `8080` inside the container.

## Docker Deployment

### Pull the Published Image

The published Docker image is available on Docker Hub:

```bash
docker pull evgen1771/spring-ai-chat:level-3
```

### Run the Container

```bash
docker run -d \
  --name spring-ai-chat \
  -p 8082:8080 \
  -e OPENROUTER_API_KEY="your_api_key" \
  evgen1771/spring-ai-chat:level-3
```

### Build and Start with Docker Compose

```bash
docker compose up -d --build
```

Check running containers:

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

Ensure the OpenRouter API key is configured in the environment used by Docker Compose.

## REST API

The application provides health check, standard chat, and RAG endpoints.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Checks application availability |
| POST | `/api/chat` | Processes a standard AI chat request |
| POST | `/api/rag` | Processes a request using retrieved document context |

The following examples assume the application is available at `http://localhost:8082`.

### Health Check

**Endpoint**

```http
GET /api/health
```

**Request**

```bash
curl http://localhost:8082/api/health
```

**Example response**

```text
OK
```

The health endpoint can be used to verify that the application is running and accessible.

### Chat Endpoint

**Endpoint**

```http
POST /api/chat
Content-Type: application/json
```

**Request body**

```json
{
  "message": "How to create REST API with Spring Boot?"
}
```

**Example request**

```bash
curl -s --max-time 120 \
  -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How to set up CI/CD?"}'
```

**Example response**

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

The response illustrates structured output containing a generated answer and parsed technical information. Actual content depends on the user request and model response.

### RAG Endpoint

**Endpoint**

```http
POST /api/rag
Content-Type: application/json
```

**Request body**

```json
{
  "message": "What is RAG?"
}
```

#### Sending UTF-8 Requests

For requests containing Cyrillic text in Git Bash or other Windows shell environments, generate the JSON request using `jq` to help avoid shell-encoding issues.

```bash
printf '%s' "Что такое RAG?" | jq -Rs '{message: .}' > request.json
```

Send the request:

```bash
curl -s --max-time 120 \
  -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json" \
  --data-binary @request.json
```

To display only the generated reply:

```bash
curl -s --max-time 120 \
  -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json" \
  --data-binary @request.json | jq -r '.reply'
```

Use `Content-Type: application/json` as shown in the examples. If requests fail with `400 Bad Request`, verify the request encoding and the content type expected by the application.

**Example response**

```json
{
  "reply": "RAG (Retrieval-Augmented Generation) is an approach in which relevant documents are retrieved and provided to a language model before it generates a response.",
  "sources": [
    {
      "id": "example-document-id",
      "content": "RAG (Retrieval-Augmented Generation) is an approach...",
      "source": "sample.txt",
      "chunkIndex": 1
    }
  ],
  "model": "nvidia/nemotron-3.5-lightning:free"
}
```

The identifiers and response text above are illustrative. Actual source metadata and content depend on the documents retrieved by the application.

The RAG endpoint retrieves relevant information from the local document collection and adds it to the prompt before sending the request to the language model.

## Retrieval-Augmented Generation

### Overview

Retrieval-Augmented Generation (RAG) allows the application to supplement a language model's response with information retrieved from a local document collection.

Instead of relying exclusively on information already available to the model, the application performs the following steps:

1. Loads text from a TXT resource.
2. Splits the document into smaller paragraphs or chunks.
3. Generates vector representations using the custom embedding implementation.
4. Stores document vectors in `SimpleVectorStore`.
5. Searches for content relevant to the user's request.
6. Adds retrieved context to the AI prompt.
7. Sends the augmented prompt to the language model.
8. Returns the generated response and source metadata.

### RAG Pipeline

```mermaid
flowchart TD
    Document[TXT Document]
    Loader[Document Loading]
    Splitter[Text Splitting]
    Embedding[Custom Embeddings]
    Store[SimpleVectorStore]
    Query[User Question]
    Search[Similarity Search]
    Context[Retrieved Context]
    Prompt[Augmented Prompt]
    LLM[OpenRouter LLM]
    Answer[Generated Response]

    Document --> Loader
    Loader --> Splitter
    Splitter --> Embedding
    Embedding --> Store

    Query --> Search
    Store --> Search
    Search --> Context
    Context --> Prompt
    Query --> Prompt
    Prompt --> LLM
    LLM --> Answer
```

### Vector Store

The application uses Spring AI's `SimpleVectorStore` as an in-memory vector storage implementation.

The vector store maintains document content and its corresponding vector representations for similarity search.

**Characteristics**

- In-memory storage.
- No external vector database required.
- Simple integration with Spring AI.
- Suitable for demonstrations and development.
- Data is not persistent across application restarts unless persistence is explicitly implemented.

### Custom Embedding Implementation

The project uses a custom hash-based embedding implementation to convert text into numerical vectors.

This implementation demonstrates how embedding generation can be integrated into a RAG pipeline without requiring a separate commercial embedding provider.

However, hash-based embeddings are not equivalent to production-grade semantic embeddings. They do not reliably capture the meaning of natural language or the semantic relationships between technical concepts.

For more meaningful semantic search, consider integrating a dedicated embedding model, such as one provided by Ollama or an embedding API.

### Document Processing

| Stage | Description |
|---|---|
| Document Loading | Reads text from a resource file |
| Text Splitting | Divides the document into paragraphs or smaller chunks |
| Embedding | Converts text into numerical vectors |
| Vector Storage | Stores vectors and associated document content |
| Similarity Search | Finds content relevant to the user query |
| Context Injection | Adds retrieved content to the AI prompt |
| Response Generation | Generates an answer using the language model |

## Function Calling

### Overview

Function Calling allows a language model to request the execution of application-defined functions when processing a user request.

The application implements and executes these functions and returns their results to the AI processing flow.

The project includes three functions:

| Function | Description | Example Request |
|---|---|---|
| `getCurrentTime` | Returns the current system time | "What time is it?" |
| `calculate` | Performs arithmetic operations | "What is 5 + 3?" |
| `getSystemInfo` | Returns system-related information | "What system are you running on?" |

### Example: Current Time

Create a UTF-8 request:

```bash
printf '%s' "Который час?" | jq -Rs '{message: .}' > request.json
```

Send the request to the RAG endpoint:

```bash
curl -s --max-time 120 \
  -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json" \
  --data-binary @request.json | jq -r '.reply'
```

Illustrative response:

```text
Функция getCurrentTime вернула: Текущее время: 20.10.2026 10:29:46
```

The displayed time is an example. The actual response depends on the system time when the function executes.

### Example: Arithmetic

```bash
printf '%s' "Сколько будет 5 + 3?" | jq -Rs '{message: .}' > request.json

curl -s --max-time 120 \
  -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json" \
  --data-binary @request.json | jq -r '.reply'
```

Illustrative response:

```text
Функция calculate вернула: 8.0
```

Supported arithmetic operations:

- Addition (`+`).
- Subtraction (`-`).
- Multiplication (`*`).
- Division (`/`).
- Decimal numbers.

Division by zero and invalid inputs should be handled appropriately by the application.

### Example: System Information

```bash
printf '%s' "Какая у тебя система?" | jq -Rs '{message: .}' > request.json

curl -s --max-time 120 \
  -X POST http://localhost:8082/api/rag \
  -H "Content-Type: application/json" \
  --data-binary @request.json | jq -r '.reply'
```

Illustrative response:

```text
Функция getSystemInfo вернула: Spring AI Chat Application | Java 17 | Spring Boot 3.2.x | OpenRouter
```

### Function Calling Flow

```mermaid
flowchart TD
    User[User Request]
    Model[Language Model]
    Decision{Is a function needed?}
    Select[Select Function and Arguments]
    Validate[Validate Arguments]
    Execute[Execute Application Function]
    Result[Return Function Result]
    Final[Generate Final Response]

    User --> Model
    Model --> Decision
    Decision -- No --> Final
    Decision -- Yes --> Select
    Select --> Validate
    Validate --> Execute
    Execute --> Result
    Result --> Model
    Model --> Final
```

### Responsibilities

**The language model:**

- Determines whether a function may be useful.
- Selects an appropriate function.
- Provides the required arguments.

**The application:**

- Registers available functions.
- Validates function arguments.
- Executes the requested operation.
- Returns the function result to the model.
- Controls access to application functionality.

Function execution must be validated by the application. Model-generated function names and arguments should not be trusted without validation.

## Structured AI Responses

The chat endpoint returns a structured response containing the generated answer and parsed technical information.

Depending on the DTO and structured output configuration, the response may contain:

- A summary.
- Recommendations.
- Difficulty level.
- Related technologies.

Example response structure:

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

The fields and content depend on the application's response models and the output produced by the language model.

## Configuration

### Environment Variables

| Variable | Description | Required |
|---|---|---|
| `OPENROUTER_API_KEY` | API key used to access the OpenRouter API | Yes |
| `SERVER_PORT` | Application server port, where supported by the configuration | No |

The application port is determined by the project configuration. The Docker examples map host port `8082` to container port `8080`.

### OpenRouter Integration

The project uses OpenRouter as the LLM provider, with the configured model:

```text
nvidia/nemotron-3.5-lightning:free
```

An illustrative configuration is shown below. The actual configuration keys must match the Spring AI version and the application's existing configuration files.

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
          model: nvidia/nemotron-3.5-lightning:free
          temperature: 0.0

server:
  port: ${SERVER_PORT:8080}
```

Do not replace the working project configuration with this example without verifying that its property names match the application's implementation.

## Testing

The project includes automated tests for document processing, Function Calling, and functional validation of AI responses.

### Unit Tests

| Metric | Result |
|---|---:|
| Test Cases | 2 |
| Passed | 2 |
| Failed | 0 |
| Success Rate | 100% |

Test cases:

- `testDocumentLoading()` — verifies document loading and chunk splitting.
- `testFunctionCalling()` — validates `getCurrentTime`, `calculate`, and `getSystemInfo`.

Run the tests locally:

```bash
mvn test
```

Alternatively, run Maven tests inside a temporary Docker container:

```bash
docker run --rm \
  -v "$(pwd):/app" \
  -w /app \
  maven:3.9.6-eclipse-temurin-17-alpine \
  mvn test
```

Illustrative output:

```text
[INFO] Running com.example.springai.service.RAGServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Functional Test Suite

| Metric | Result |
|---|---:|
| Functional Test Cases | 15 |
| Passed | 15 |
| Failed | 0 |
| Success Rate | 100% |

The functional test suite validates response processing across multiple technical topics:

- Spring Boot REST API.
- Docker.
- Python.
- CI/CD.
- Microservices.
- Kubernetes.
- Git.
- Database selection.
- REST API security.
- GraphQL vs. REST.
- Unit testing.
- Caching.
- Asynchronous programming.
- Cloud platforms.
- Java interview preparation.

Successful responses were expected to contain structured information, including a summary, recommendations, a difficulty level, and relevant technologies.

Test results reflect the documented test runs; rerun the tests to verify the current state of the codebase.

## Known Limitations

### 1. Free-Tier Model Availability and Latency

The configured free-tier model may exhibit the following issues:

- Slow response times, observed at approximately 20–45 seconds per request.
- Intermittent empty responses under load.
- Incorrect or hallucinated function names.

**Mitigation:**

- Use an extended timeout for API requests.
- Retry requests after temporary provider failures.
- Validate function names and arguments.
- Handle empty or malformed model responses.

Example:

```bash
curl --max-time 120 ...
```

The actual response time and availability depend on the provider and current service conditions.

### 2. Hash-Based Embeddings

The custom hash-based embedding implementation is intended to demonstrate vector storage and retrieval, not to provide high-quality semantic search.

It may fail to distinguish semantically different concepts or identify related technical terminology. For example, queries involving embeddings or Docker may retrieve irrelevant document chunks even when more relevant content exists in the document collection.

**Recommendation:** Replace the custom implementation with a dedicated semantic embedding model, such as one available through Ollama or an embedding API.

### 3. In-Memory Vector Storage

`SimpleVectorStore` stores vector data in memory. The indexed document collection is not persistent across application restarts unless persistence is explicitly implemented.

**Recommendation:** Consider a persistent vector database for applications that require durable document indexes.

### 4. Production Readiness

The project is suitable for learning, demonstration, and further development. Additional reliability, security, and operational controls are recommended before production use.

## Project Requirements

| Requirement | Status |
|---|---|
| Spring Boot REST API | Implemented |
| Chat endpoint | Implemented |
| RAG endpoint | Implemented |
| PromptTemplate support | Implemented |
| Structured Output mapping | Implemented |
| OpenRouter API integration | Implemented |
| SimpleVectorStore integration | Implemented |
| Custom embedding implementation | Implemented |
| TXT document processing | Implemented |
| Function Calling | Implemented |
| JUnit tests | Implemented |
| Docker containerization | Implemented |
| Linux VPS deployment | Implemented |
| Health check endpoint | Implemented |
| GitHub repository | Available |

## Deployment

The application is deployed on a Linux VPS using Docker Compose.

### Local Endpoints

The following endpoints are examples for a local deployment with host port `8082`:

| Endpoint | Method | Description |
|---|---|---|
| `http://localhost:8082/api/health` | GET | Health check |
| `http://localhost:8082/api/chat` | POST | Standard AI chat |
| `http://localhost:8082/api/rag` | POST | Retrieval-Augmented Generation |

These URLs refer to the local host and are not public live-demo links.

### Deployment Components

- Docker Compose orchestration.
- Containerized Spring Boot application.
- Environment-based configuration.
- OpenRouter API integration.
- Health check endpoint.
- RAG processing.
- Function Calling.
- Application logs.
- Remote Linux deployment.

## Production Considerations

Before using the application in a production environment, consider implementing the following improvements:

- Secure secret management.
- Authentication and authorization.
- Request rate limiting.
- Input validation.
- Consistent error handling.
- Persistent vector storage.
- Monitoring and alerting.
- Centralized logging.
- API documentation.
- Automated CI/CD pipelines.
- Model availability and fallback handling.
- More comprehensive unit and integration tests.

## Future Improvements

Potential future enhancements include:

- Streaming responses with reactive types such as `Flux`.
- User authentication and authorization.
- Conversation history.
- Persistent chat memory.
- PostgreSQL integration.
- Persistent vector database.
- Improved semantic embeddings.
- Support for additional document formats.
- File upload endpoint for RAG documents.
- Multiple LLM providers.
- Swagger / OpenAPI documentation.
- Extended unit and integration testing.
- GitHub Actions CI/CD.
- Kubernetes deployment.
- Application metrics.
- Centralized logging.
- Advanced error handling.
- Request rate limiting.
- Improved RAG evaluation.

## Learning Objectives

This project demonstrates practical experience with:

- Building REST APIs using Spring Boot.
- Integrating Spring AI into Java applications.
- Working with LLM providers through OpenRouter.
- Creating prompts with `PromptTemplate`.
- Mapping AI responses to Java objects.
- Implementing a basic RAG pipeline.
- Working with vector storage.
- Creating custom embeddings.
- Processing text documents.
- Implementing Function Calling.
- Writing JUnit tests for AI functionality.
- Containerizing Java applications.
- Deploying applications to a Linux VPS.
- Validating AI response processing through functional tests.

## Author

**Evgen242**

GitHub: [https://github.com/Evgen242](https://github.com/Evgen242)

Project Repository: [spring-ai-chat](https://github.com/Evgen242/spring-ai-chat)

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
