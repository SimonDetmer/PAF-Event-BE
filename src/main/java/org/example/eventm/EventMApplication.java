package org.example.eventm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventMApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventMApplication.class, args);
    }
}
