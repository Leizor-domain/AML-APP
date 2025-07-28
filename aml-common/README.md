# AML Common Module

Shared utilities, security components, and common entities used across the AML Engine system. This module provides centralized functionality for authentication, authorization, and common business logic.

## 🚀 Overview

The `aml-common` module serves as the foundation for shared functionality across all AML Engine modules. It contains core security components, common entities, and utility classes that are used by `aml-admin`, `aml-portal`, and other modules.

## 🏗️ Architecture

### Core Components

#### **Security & Authentication**
- **JWT utilities** for token generation and validation
- **User authentication** components and filters
- **Role-based access control** enums and utilities
- **Security configuration** helpers

#### **Common Entities**
- **User management** entities and DTOs
- **Shared enums** for system-wide constants
- **Exception handling** with standardized error codes
- **Validation utilities** for data integrity

#### **Utility Classes**
- **Common validation** and formatting utilities
- **Shared constants** and configuration
- **Cross-module** communication helpers

## 📁 Module Structure

```
aml-common/
├── src/main/java/com/leizo/common/
│   ├── entity/                          # Shared JPA entities
│   │   └── Users.java                   # User entity for all modules
│   ├── enums/                           # System-wide enums
│   │   └── UserRole.java                # User role definitions
│   ├── repository/                      # Shared repositories
│   │   └── UserRepository.java          # User data access
│   ├── security/                        # Security components
│   │   ├── JwtAuthFilter.java           # JWT authentication filter
│   │   ├── JwtUtil.java                 # JWT utility functions
│   │   └── UserDetailsServiceImpl.java  # User details service
│   └── util/                            # Utility classes
│       └── ValidationUtils.java         # Common validation logic
├── src/main/resources/
│   └── data/                            # Static data files
└── src/test/java/                       # Test classes
```

## 🔧 Key Features

### User Management
- **Centralized user entity** used across all modules
- **Role-based permissions** with standardized enums
- **JWT token management** with secure validation
- **User authentication** with BCrypt password hashing

### Security Components
- **JWT authentication filter** for request processing
- **User details service** for Spring Security integration
- **Role-based access control** with enum definitions
- **Secure token utilities** for JWT operations

### Common Utilities
- **Validation helpers** for data integrity
- **Shared constants** for system-wide configuration
- **Exception handling** with standardized error codes
- **Cross-module** communication utilities

## 🔌 Integration

### Dependencies
```xml
<dependency>
    <groupId>com.leizo</groupId>
    <artifactId>aml-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage in Other Modules
```java
// Import shared entities
import com.leizo.common.entity.Users;
import com.leizo.common.enums.UserRole;

// Use security components
import com.leizo.common.security.JwtUtil;
import com.leizo.common.security.JwtAuthFilter;

// Access repositories
import com.leizo.common.repository.UserRepository;
```

## 🔒 Security Features

### JWT Authentication
```java
// Token generation
String token = JwtUtil.generateToken(userDetails);

// Token validation
boolean isValid = JwtUtil.validateToken(token, userDetails);

// Extract user information
String username = JwtUtil.extractUsername(token);
```

### Role-Based Access Control
```java
// User role definitions
public enum UserRole {
    ADMIN,      // Full system access
    ANALYST,    // Transaction analysis and alert review
    SUPERVISOR, // Team oversight and escalation
    VIEWER      // Read-only access
}

// Role checking
if (user.getRole() == UserRole.ADMIN) {
    // Admin-specific logic
}
```

### User Authentication
```java
// User entity with security features
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password; // BCrypt hashed
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    // ... other fields and methods
}
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JwtUtilTest

# Run with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Test with other modules
mvn test -Dtest="*IntegrationTest"
```

## 📊 Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin

# Logging
logging.level.com.leizo.common=DEBUG
```

### Environment Variables
```bash
# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aml_database
DB_USERNAME=aml_user
DB_PASSWORD=your_password
```

## 🔧 Development

### Adding New Features
1. **Create feature** in appropriate package
2. **Add unit tests** for new functionality
3. **Update documentation** with usage examples
4. **Test integration** with other modules

### Code Standards
- **Follow Java naming conventions**
- **Add comprehensive documentation**
- **Include unit tests** for all new code
- **Use proper exception handling**

## 🚀 Deployment

### Build Process
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package for deployment
mvn package
```

### Integration with Other Modules
```bash
# Install to local repository
mvn clean install

# Use in other modules
mvn dependency:resolve
```

## 🔧 Troubleshooting

### Common Issues

#### JWT Token Problems
```bash
# Check JWT secret configuration
echo $JWT_SECRET

# Verify token expiration
curl -H "Authorization: Bearer <token>" http://localhost:8080/alerts
```

#### User Authentication Issues
```bash
# Check user credentials
psql -U aml_user -d aml_database -c "SELECT username, role FROM users;"

# Verify password hashing
# Passwords should be BCrypt hashed, not plain text
```

#### Module Integration Issues
```bash
# Check module dependencies
mvn dependency:tree

# Verify classpath
mvn dependency:resolve
```

## 📚 API Reference

### JWT Utilities
```java
public class JwtUtil {
    // Generate JWT token
    public static String generateToken(UserDetails userDetails);
    
    // Validate JWT token
    public static boolean validateToken(String token, UserDetails userDetails);
    
    // Extract username from token
    public static String extractUsername(String token);
    
    // Extract expiration date from token
    public static Date extractExpiration(String token);
}
```

### User Entity
```java
public class Users {
    // Get user ID
    public Integer getId();
    
    // Get username
    public String getUsername();
    
    // Get user role
    public UserRole getRole();
    
    // Check if user is enabled
    public boolean isEnabled();
}
```

### User Role Enum
```java
public enum UserRole {
    ADMIN,      // Full system access
    ANALYST,    // Transaction analysis
    SUPERVISOR, // Team oversight
    VIEWER      // Read-only access
}
```

## 📈 Performance

### Optimization Tips
- **Use connection pooling** for database operations
- **Implement caching** for frequently accessed data
- **Optimize JWT operations** with efficient algorithms
- **Monitor memory usage** for large user datasets

### Monitoring
- **JWT token performance** metrics
- **User authentication** response times
- **Database query** optimization
- **Memory usage** patterns

## 🔒 Security Best Practices

### JWT Security
- **Use strong secret keys** for JWT signing
- **Implement token expiration** with reasonable timeouts
- **Validate tokens** on every request
- **Store secrets securely** using environment variables

### User Security
- **Hash passwords** using BCrypt with salt
- **Implement account lockout** for failed login attempts
- **Use HTTPS** for all communications
- **Log security events** for audit purposes

### Data Protection
- **Validate all inputs** to prevent injection attacks
- **Use parameterized queries** for database operations
- **Implement proper error handling** without exposing sensitive data
- **Encrypt sensitive data** at rest and in transit

---

**AML Common Module - Foundation for shared functionality across the AML Engine system**
