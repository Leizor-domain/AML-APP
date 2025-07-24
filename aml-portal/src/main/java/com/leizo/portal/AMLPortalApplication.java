package com.leizo.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.leizo.portal",
    "com.leizo.common.security",
    "com.leizo.common.repository",
    "com.leizo.common.entity"
})
@EnableJpaRepositories(basePackages = "com.leizo.common.repository")
@EntityScan(basePackages = "com.leizo.common.entity")
public class AMLPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AMLPortalApplication.class, args);
    }
}
