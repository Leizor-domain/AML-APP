package com.leizo.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//Spring boot Admin Application
@SpringBootApplication(scanBasePackages = {"com.leizo"})
@EnableJpaRepositories(basePackages = {"com.leizo.admin.repository"})
public class AMLAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AMLAdminApplication.class, args);
    }
}
