package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 어플리케이션 진입점
 */
@SpringBootApplication
public class DistributedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedServiceApplication.class, args);
    }
}