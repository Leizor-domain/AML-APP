package com.leizo.repository;

/*
public class DBConfig {

    public static final String URL = "jdbc:postgresql://localhost:5432/amlengine_db";

    public static final String USER = "postgres";

    public static final String PASSWORD = "1234";

}


/*
🔐 Security Note:
Although this structure works for local development, you should never commit real passwords to a repository. For Spring Boot, consider using:

properties
Copy
Edit
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/amlengine_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
Then set DB_PASSWORD in a .env file or environment variable for safer config.
 */
