# SwiftLink: Entterprise URL Shortener
SwiftLink is a production-grade, distributed URL shortening platform designed to handle high traffic, ensure secure authentication, and process analytics asynchronously at scale.

Built using modern backend principles, this project demonstrates microservices architecture, event-driven design, caching, and rate limiting, similar to real-world systems.
## 🔥 Key Features
- **🔐 Centralized Authentication Service**
    - Dedicated Security Service for JWT validation across microservices
    - OAuth2 + JWT with HttpOnly cookies for enhanced security
    - Eliminates duplicated auth logic in downstream services
- **⚡Scalable URL Shortening**
    - **Distributed ID generation** for unique short codes
    - Designed to avoid collisions across services
- **🚀 High Performance with Caching**
    - Redis caching for frequently accessed URLs
    - Reduces database load and improves response time
- **📊 Event-Driven Analytics**
    - Kafka-based architecture
    - URL Service → produces click events
    - Analytics Service → consumes and processes asynchronously
- **🛡 Rate Limiting**
    - Token Bucket algorithm
    - Configured at API Gateway: 10 req/sec (burst up to 20)
- **🌐 API Gateway(Reactive)**
    - Built with Spring Cloud Gateway (WebFlux)
    - Handles routing, authentication, and rate limiting
- **🔄 Service Discovery**
    - Eureka for dynamic service registration

## 🏗 System Design
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
## 🛠 Tech Stack
### Backend
- Java 17
- Spring Boot 3.x
- Spring Cloud (Gateway, Eureka)
- Spring Security (OAuth2 + JWT)
- Kafka (Event Streaming)
- Redis (Caching)
- MongoDB / MySQL
- Maven
### Frontend
- React.js
- Context API (State Management)
- Lucide Icons
- Sonner / SweetAlert2
## Project Structure
```text
url-shortener/
├── backend/
│   ├── api-gateway/       # Spring Cloud Gateway (Port: 9096)
│   ├── service-registry/  # Eureka Server (Port: 8761)
|   ├── security-service/
│   ├── user-service/      # Auth & User Mgmt (Port: 9098)
│   ├── url-service/       # Shortening Logic (Port: 9094)
│   └── analytics-service/ # Tracking Logic
└── frontend/
    └── url-Shortener-ui/      # React + Lucide + Sonner
```
## 🚀 Getting Started
### Prerequisites
- JDK 17+
- Node.js 18+
- Maven 3.8+
- Redis
- Kafka

## Installation
- **1. Clone the repo:**
``` Text
  git clone https://github.com/Hemant15-Bl/Url-Shortener.git
  cd Url-Shortener
```
- **2. Run Backend Services:**
Start the services in this order: Service Registry -> API Gateway -> User/URL Services.
``` Bash
cd backend/service-registry
mvn spring-boot:run
```
- **3. Run Frontend**
``` Bash
cd frontend/url-Shortener-ui
npm install
npm run dev
```
## 📈 Future Improvements
- Geo-based analytics
- Custom aliases for URLs
- Multi-region deployment
- Circuit breakers (Resilience4j)
- Kubernetes deployment

## 💡 Engineering Decisions
- **Why Kafka?**
    -- Decouples analytics from request lifecycle
    -- Improves performance and reliability
- **Why Redis?**
    -- Reduces DB hits for frequently accessed URLs
- **Why Security Service?**
    -- Avoids duplication and centralizes authentication logic
- **Why Rate Limiting?**
    -- Protects system from abuse and ensures fair usage

## 📸 Screenshots
![image alt](https://github.com/Hemant15-Bl/Url-Shortener/blob/main/home.jpeg?raw=true)
![image alt](https://github.com/Hemant15-Bl/Url-Shortener/blob/main/signup.jpeg?raw=true)
![image alt](https://github.com/Hemant15-Bl/Url-Shortener/blob/main/signin.jpeg?raw=true)
![image alt](https://github.com/Hemant15-Bl/Url-Shortener/blob/main/dashboard.jpeg?raw=true)
![image alt](https://github.com/Hemant15-Bl/Url-Shortener/blob/main/analytics.jpeg?raw=true)

## 👨‍💻 Author
Hemant Barole
GitHub: https://github.com/Hemant15-Bl
