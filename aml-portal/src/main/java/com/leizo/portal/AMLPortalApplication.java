package com.leizo.portal;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Spring boot Portal Application
@SpringBootApplication(scanBasePackages = {
        "com.leizo.portal",
        "com.leizo.common.security" // <- INCLUDE THIS
})
public class AMLPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AMLPortalApplication.class, args);
    }
}
