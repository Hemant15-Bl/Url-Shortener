# 🚀 SwiftLink — Distributed URL Shortener Platform

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0-6DB33F?logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-Vite-61DAFB?logo=react&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

SwiftLink is a production-grade, distributed URL shortening platform designed to handle high traffic, ensure secure authentication, and process analytics asynchronously at scale.
Built using modern backend principles, this project demonstrates microservices architecture, event-driven design, caching, and rate limiting, similar to real-world systems.

---

<p align="center">
  <img src="https://github.com/Hemant15-Bl/Url-Shortener/blob/main/home.jpeg?raw=true" alt="Url Shortener landing page" width="85%">
</p>

---

## 🔥 Key Features
- **🔐 Centralized Authentication Service**
  - Dedicated Security Service for JWT validation across microservices
  - OAuth2 + JWT with HttpOnly cookies for enhanced security
  - Eliminates duplicated auth logic in downstream services
- **⚡ Scalable URL Shortening**
  - Distributed ID generation for unique short codes
  - Designed to avoid collisions across services
- **📊 Event-Driven Analytics**
  - Kafka-based architecture
  - URL Service → produces click events
  - Analytics Service → consumes and processes asynchronously
- **🚀 High Performance with Caching**
  - Redis caching for frequently accessed URLs
  - Reduces database load and improves response time
- **🛡 Rate Limiting**
  - Token Bucket algorithm
  - Configured at API Gateway: 10 req/sec (burst up to 20)
- **🌐 API Gateway (Reactive)**
  - Built with Spring Cloud Gateway (WebFlux)
  - Handles routing, authentication, and rate limiting
- **🔄 Service Discovery**
  - Eureka for dynamic service registration

---

## 🏗️ System Design
### Core Services
- **API Gateway**
  - Entry point for all requests
  - Implements:
    - JWT validation via Security Service
    - Rate limiting
    - Request routing
- **Security Service**
  - Centralized authentication & token validation
  - Shared across all services
- **User Service**
  - User registration and login
- **URL Service**
  - Generates short URLs using distributed ID strategy
  - Publishes events to Kafka
- **Analytics Service**
  - Consumes Kafka events
  - Processes click data asynchronously
- **Service Registry (Eureka)**
  - Enables service discovery and scaling

## ⚙️ Architecture Highlights
- **Microservices + Event-Driven Design**
- **Centralized Security Layer**
- **Redis Caching for Low Latency**
- **Kafka for Async Processing**
- **Rate Limiting for Abuse Prevention**
- **Stateless Services → Horizontal Scaling**

---

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3.x , Maven |
| Microservices | Spring Cloud: Eureka, Gateway (reactive), OpenFeign |
| Security | Spring Security, OAuth2.O, Authentication/JWT |
| Frontend | React, Axios, React Router, Bootstrap (reactstrap), Sonner / SweetAlert2, Lucide Icons |
| Databases | MySQL (users, url) , MongoDB (analytics), Redis (rate limiting, url cached) |
| DevOps | Docker, Docker Compose, Docker Hub images |

---

## Architecture

```mermaid
graph TB
    %% 1. Traffic ingress
    Browser["Browser"] --> UI["React UI :3000"]
    JMeter["50 Concurrent Users<br/>(JMeter)"]

    %% 2. API Gateway
    subgraph GWL["API Gateway Layer (Spring Cloud Gateway)"]
        AUTH["A. Authentication Filter"]
        RL["B. Request Rate Limiter<br/>(Token Bucket Algorithm)"]
        AUTH --> RL
    end
    UI --> AUTH
    JMeter --> AUTH

    %% 3. Control & caching plane
    subgraph CCP["Control & Caching Plane"]
        EUREKA["Eureka Server<br/>(Service Discovery)"]
        USER["User-Service<br/>(Authentication / JWT)"]
        REDIS[("Redis Server<br/>State & Caching<br/>Token Bucket: R=10, B=20<br/>URL Cache: TTL 1hr")]
    end

    %% 4. Link & analytics plane
    subgraph LAP["Link & Analytics Plane"]
        URL["URL-Service<br/>(Link Resolution)<br/>3 instances"]
        ANALYTICS["Analytics-Service<br/>(Event Processing)"]
    end

    %% 5. Message broker
    KAFKA{{"Kafka Cluster<br/>(Async Messaging)"}}

    %% 6. Data persistence
    subgraph DB["Data Persistence Layer (HikariCP, Max=50)"]
        USERDB[("User DB<br/>Auth Data")]
        URLDB[("URL DB<br/>Link Data")]
        ANALYTICSDB[("Analytics DB<br/>Click Logs")]
    end

    %% Gateway routing
    RL -->|"Token bucket counters"| REDIS
    RL --> USER
    RL --> URL
    RL --> ANALYTICS

    %% Service discovery
    GWL -.->|"Registration"| EUREKA
    USER -.->|"Registration"| EUREKA
    URL -.->|"Registration"| EUREKA
    ANALYTICS -.->|"Registration"| EUREKA

    %% Caching
    USER -->|"Cache"| REDIS
    URL <-->|"Cache read/write"| REDIS
    REDIS --> ANALYTICS

    %% Event pipeline
    URL -->|"Publish Click Event"| KAFKA
    KAFKA -->|"Consume Click Event"| ANALYTICS

    %% Persistence
    USER --> USERDB
    URL --> URLDB
    ANALYTICS --> ANALYTICSDB
```

## 📁 Project Structure
```Text
url-shortener/
├── backend/
│   ├── api-gateway/
│   ├── service-registry/
│   ├── security-service/
│   ├── user-service/
│   ├── kgs-service/
│   ├── url-service/
│   └── analytics-service/
└── frontend/
    └── url-shortener-ui/
```

---

## 🚀 Getting Started
### Prerequisites
- **Docker Desktop** (8 GB RAM allocated or more is recommended)
- **Git**
- **Node.js and npm** (only if you run the frontend manually)

### 1. Clone the repository
```Bash
git clone https://github.com/Hemant15-Bl/Url-Shortener.git
cd Url-Shortener
```

### 2. Start the application

The startup script launches the services in the order they need (Eureka, Kafka & Redis, Security Service, then the rest):

```bash
# Windows
start-app.bat
```

Or start everything directly with Docker Compose:

```bash
docker-compose up -d
```
**Startup time**

- **8 GB RAM systems:** about **4-5 minutes** with the WSL memory settings optimization. Without them the first boot can take **8-9 minutes**, because 8 or more JVMs start and run database migrations.
- **16 GB+ RAM systems:** expect about **2-3 minutes**.

### 3. Open the application

Once the containers show as healthy in Docker Desktop:

| What | URL |
|---|---|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8084 |
| Eureka dashboard | http://localhost:8761 |

---

## 📈 Future Improvements
- Geo-based analytics
- Custom aliases for URLs
- Multi-region deployment
- Circuit breakers (Resilience4j)
- Kubernetes deployment

---

## 💡 Engineering Decisions
- **Why Kafka?**
  - Decouples analytics from request lifecycle
  - Improves performance and reliability
- **Why Redis?**
  - Reduces DB hits for frequently accessed URLs
- **Why Security Service?**
  - Avoids duplication and centralizes authentication logic
- **Why Rate Limiting?**
  - Protects system from abuse and ensures fair usage

---

## 📸 Screenshots

<table>
  <tr>
    <td align="center" width="50%">
      <img src="https://github.com/Hemant15-Bl/Url-Shortener/blob/main/signin.jpeg?raw=true" alt="Sign in page"><br>
      <sub>Sign in</sub>
    </td>
    <td align="center" width="50%">
      <img src="https://github.com/Hemant15-Bl/Url-Shortener/blob/main/signup.jpeg?raw=true" alt="Create account page"><br>
      <sub>Create account</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="https://github.com/Hemant15-Bl/Url-Shortener/blob/main/dashboard.jpeg?raw=true" alt="User dashboard with short urls"><br>
      <sub>Dashboard</sub>
    </td>
    <td align="center" width="50%">
      <img src="https://github.com/Hemant15-Bl/Url-Shortener/blob/main/analytics.jpeg?raw=true" alt="analytics logs"><br>
      <sub>Analytics Logs</sub>
    </td>
  </tr>
</table>

---

## 👨‍💻 Author
Built by [Hemant15-Bl](https://github.com/Hemant15-Bl). Docker images: [hemantbarole](https://hub.docker.com/u/hemantbarole).

