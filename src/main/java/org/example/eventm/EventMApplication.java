package org.example.eventm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = "org.example.eventm")
public class EventMApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventMApplication.class, args);
    }
}
