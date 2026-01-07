# User Service

## About the Project

The **User Service** is a Spring Boot-based microservice that provides user authentication, authorization, and management functionalities. It supports features like user registration, login, email verification, password reset, and OAuth2-based social login (Google, Facebook, GitHub). The service uses PostgreSQL as the database and integrates with Spring Security for authentication and authorization.

---

## Features

- **User Registration** with email verification.
- **User Login** with JWT-based authentication.
- **Password Reset** functionality.
- **OAuth2 Login** with Google, Facebook, and GitHub.
- **Role-based Authorization** for accessing protected resources.
- **RESTful APIs** with OpenAPI documentation.
- **Dockerized Deployment** with PostgreSQL and PgAdmin.

---

## Prerequisites

Before starting, ensure you have the following installed:

- **Java 21** or higher
- **Gradle**
- **Docker** and **Docker Compose**
- **PostgreSQL** (if running locally without Docker)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-repo/user-service.git
cd user-service
```

---

### 2. API Endpoints

Below is a list of the main API endpoints provided by the User Service, along with their input and output formats:

#### User Management

- **POST /api/v1/users/register**  
    **Input:**  
    ```json
    {
      "email": "user@example.com",
      "password": "password123",
      "name": "John Doe"
    }
    ```  
    **Output:**  
    ```json
    {
      "message": "User registered successfully. Please verify your email."
    }
    ```

- **POST /api/v1/users/login**  
    **Input:**  
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```  
    **Output:**  
    ```json
    {
      "token": "jwt-token-here",
      "expiresIn": 3600
    }
    ```

- **POST /api/v1/users/password-reset**  
    **Input:**  
    ```json
    {
      "email": "user@example.com"
    }
    ```  
    **Output:**  
    ```json
    {
      "message": "Password reset link sent to your email."
    }
    ```

- **PUT /api/v1/users/password-reset/confirm**  
    **Input:**  
    ```json
    {
      "token": "reset-token-here",
      "newPassword": "newPassword123"
    }
    ```  
    **Output:**  
    ```json
    {
      "message": "Password reset successful."
    }
    ```

#### OAuth2 Login

- **GET /oauth2/authorize/{provider}**  
    **Input:**  
    No request body required.  
    **Output:**  
    Redirects to the OAuth2 provider's login page.

#### Role Management

- **GET /api/v1/roles**  
    **Input:**  
    No request body required.  
    **Output:**  
    ```json
    [
      {
        "id": 1,
        "name": "ROLE_USER"
      },
      {
        "id": 2,
        "name": "ROLE_ADMIN"
      }
    ]
    ```

- **POST /api/v1/roles**  
    **Input:**  
    ```json
    {
      "name": "ROLE_MANAGER"
    }
    ```  
    **Output:**  
    ```json
    {
      "id": 3,
      "name": "ROLE_MANAGER"
    }
    ```

For detailed API documentation, refer to the OpenAPI specification available at `/swagger-ui.html` after running the service.
