package com.leizo.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

//Spring boot Admin Application
@SpringBootApplication(scanBasePackages = {
    "com.leizo.admin",
    "com.leizo.common.security",
    "com.leizo.common.repository",
    "com.leizo.common.entity",
    "com.leizo.pojo.entity",
    "com.leizo.service",
    "com.leizo.admin.repository"
})
@ComponentScan(basePackages = {"com.leizo"})
@EnableJpaRepositories(basePackages = {
    "com.leizo.common.repository",
    "com.leizo.admin.repository"
})
@EntityScan(basePackages = {
    "com.leizo.common.entity",
    "com.leizo.pojo.entity"
})
public class AMLAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AMLAdminApplication.class, args);
    }
}
