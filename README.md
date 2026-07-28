# Spring AI Chat Application

REST API application built with Spring Boot and Spring AI for interacting with Large Language Models (LLM) through Ollama.

---

## Live Demo

The application is deployed and accessible at:

| Service | URL |
|---------|-----|
| **Health Check** | [http://194.154.27.141:8082/api/health](http://194.154.27.141:8082/api/health) |

### Test the API

```bash
curl -X POST http://194.154.27.141:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "How to create REST API with Spring Boot?"}'
Overview
This project demonstrates the integration of Spring AI into a Spring Boot application.

The service accepts user requests through a REST API, generates prompts using PromptTemplate, sends them to an LLM using ChatClient, converts the response into a structured Java object, and returns the result as JSON.

The project was developed as a practical demonstration of integrating modern AI capabilities into a Java backend application.

Features
RESTful API built with Spring Boot

Spring AI integration

ChatClient API

PromptTemplate support

Structured Output mapping

Ollama integration

JSON request/response processing

Docker containerization

Docker Compose deployment

Linux VPS deployment

Technology Stack
Component	Version
Java	17
Spring Boot	3.2.x
Spring AI	0.8.x
Maven	3.9+
Ollama	Latest
LLM Model	llama3.2:1b
Docker	Latest
Architecture
text
                +----------------+
                |     Client     |
                +-------+--------+
                        |
                        v
              +-------------------+
              |  ChatController   |
              +---------+---------+
                        |
                        v
              +-------------------+
              |    ChatService    |
              +---------+---------+
                        |
          +-------------+-------------+
          |                           |
          v                           v
  PromptTemplate              ChatClient
          |                           |
          +-------------+-------------+
                        |
                        v
                 Ollama (LLM)
                        |
                        v
              ParsedResponse Model
                        |
                        v
                 JSON Response
Project Structure
text
spring-ai-chat/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/
│   │   │   │   └── ChatController.java
│   │   │   ├── model/
│   │   │   │   ├── ChatRequest.java
│   │   │   │   ├── AiChatResponse.java
│   │   │   │   └── ParsedResponse.java
│   │   │   ├── service/
│   │   │   │   └── ChatService.java
│   │   │   └── SpringAiApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
Getting Started
Clone the repository
bash
git clone https://github.com/Evgen242/spring-ai-chat.git
cd spring-ai-chat
Build the project
bash
mvn clean package
Run locally
bash
mvn spring-boot:run
The application will start on the configured port.

Run with Docker
bash
docker compose up -d --build
REST API
Health Check
text
GET /api/health
Response:

text
OK
Chat Endpoint
text
POST /api/chat
Content-Type: application/json
Request:

json
{
  "message": "How to create REST API with Spring Boot?"
}
Response:

json
{
  "reply": "Summary: To create a REST API with Spring Boot...",
  "parsedInfo": {
    "summary": "To create a REST API with Spring Boot...",
    "recommendations": [
      "Use Spring Initializr",
      "Add spring-boot-starter-web dependency"
    ],
    "difficulty": "MEDIUM",
    "technologies": [
      "Java",
      "Spring Boot",
      "Spring Web"
    ]
  }
}
Example Request
bash
curl -X POST http://194.154.27.141:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "How to create REST API with Spring Boot?"}'
Deployment
The application is deployed on a Linux VPS using Docker Compose.

Health Check: http://194.154.27.141:8082/api/health

Deployment includes:

Docker Compose orchestration

Containerized Spring Boot application

Environment-based configuration

Health monitoring

Production-ready deployment

Request Processing Flow
text
HTTP Request
      │
      ▼
ChatController
      │
      ▼
ChatService
      │
      ├── Build Prompt
      ├── Send Request
      ▼
ChatClient
      │
      ▼
Ollama (LLM)
      │
      ▼
AI Response
      │
      ▼
Structured Output Mapping
      │
      ▼
JSON Response
Build Requirements
Java 17 or newer

Maven 3.9+

Docker 24+ (optional)

Docker Compose (optional)

Ollama (optional)

llama3.2:1b model (optional)

Requirements Covered
☑ Spring Boot REST API
☑ Spring AI integration
☑ ChatClient implementation
☑ PromptTemplate usage
☑ Structured Output mapping
☑ Docker containerization
☑ Docker Compose deployment
☑ Linux VPS deployment
Author
Evgen242

GitHub: https://github.com/Evgen242

License
MIT
