# aml-admin

The `aml-admin` module handles authentication, JWT-based security, and role-based access control within the AML Engine ecosystem. It provides secured backend entry points for users based on roles such as Admin, Analyst, Supervisor, and Viewer.

---

## Key Features

- JWT-based authentication and role authorization
- Role-based endpoint access (`Admin`, `Analyst`, `Supervisor`, `Viewer`)
- Secure login and user registration logic
- Stateless Spring Security configuration
- Integrated JWT filter (`JwtAuthFilter`) for all requests
- Spring Boot modular structure

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL
- Keycloak (optional, but can be enabled)
- Environment variable: `DB_PASSWORD`

---

## Module Structure

```bash
aml-admin/
├── config/                            # Spring Security & JWT filter
│   ├── SecurityConfig.java
│   └── JwtAuthFilter.java
│
├── auth/                              # Auth-related classes
│   ├── Users.java
│   ├── UserRequest.java
│   ├── UserResponse.java
│   └── UserController.java
│
├── repository/
│   └── UserRepository.java
│
├── service/
│   ├── UserService.java
│   └── UserServiceImpl.java
│
├── AMLAdminApplication.java
└── application.properties

```

## User Management (Standard)

- Only admins can create new users via `POST /users/create` (UserRequest DTO)
- List users via `GET /users` (returns UserResponse DTOs)
- Passwords are always BCrypt-hashed and never returned in any API response
- UserRequest: `{ username, password, role }`
- UserResponse: `{ username, role, createdAt }`

### Endpoints
- `POST /users/create` (admin only): Create user
- `GET /users`: List users (paginated)
- `PATCH /users/{id}/status`: Enable/disable user
- `GET /users/role-distribution`: Get user role counts
