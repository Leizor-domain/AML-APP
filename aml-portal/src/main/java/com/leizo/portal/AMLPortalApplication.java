package com.leizo.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.leizo.portal"
})
public class AMLPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AMLPortalApplication.class, args);
    }
}
