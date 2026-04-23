# SwiftLink: Entterprise URL Shortener
### A high-performance Microservices ecosystem built with Spring Boot and React.

SwiftLink it not just a url shortener: it's a full stact demonstration of modern distributed system. It features a centralized API Gateway, secure JWT-based authentication using HttpOnly cookies, and real-time analytics tracking.

## 🏗 System Architecture
The application is split into a **Reactive API Gateway**, multiple **REST Microservices**, and a **React** Frontend, all communicating over a virtual network.

- **API Gateway:** Built with Spring Cloud Gateway (WebFlux). Handles routing, JWT validation, and session management.

- **User Service:** Manages user registration and secure authentication.

- **URL Service:** Handles the core logic of encoding long URLs into unique short codes.

- **Analytics Service:** Tracks click rates and visitor data for every shortened link.

- **Service Registry:** Eureka Discovery Server for dynamic service registration.

## Repo Structure
```text
url-shortener/
├── backend/
│   ├── api-gateway/       # Spring Cloud Gateway (Port: 9096)
│   ├── service-registry/  # Eureka Server (Port: 8761)
│   ├── user-service/      # Auth & User Mgmt (Port: 9098)
│   ├── url-service/       # Shortening Logic (Port: 9094)
│   └── analytics-service/ # Tracking Logic
└── frontend/
    └── swiftlink-ui/      # React + Vite + Lucide + Sonner
```

## 🛠 Tech Stack
### Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3.x, Spring Cloud (Gateway, Eureka)
- **Security:** Spring Security, JWT (HttpOnly Cookies)
- **Persistence:** MongoDB / MySQL (JPA/Hibernate)
- **Build Tool:** Maven

## Frontend
- **Library:** React.js
- **Styling:** Modern CSS3 (SaaS Aesthetic)
- **Icons:** Lucide-React
- **Notifications:** Sonner & SweetAlert2
- **State Management:** Hooks (Context API ready)

## Getting Started
### Prerequisites
- JDK 17+
- Node.js 18+
- Maven 3.8+

## Installation
- **1.Clone the repo:**
  ```bash
  git clone https://github.com/Hemant15-Bl/Url-Shortener.git
  cd Url-Shortener
  ```
- **2. Run Backend Services:**
Start the services in this order: Service Registry -> API Gateway -> User/URL Services.
```bash
cd backend/service-registry
mvn spring-boot:run
```
- **3. Run Frontend**
  ```bash
cd frontend/url-Shortener-ui
npm install
npm run dev
```

## 📸 Screenshots
