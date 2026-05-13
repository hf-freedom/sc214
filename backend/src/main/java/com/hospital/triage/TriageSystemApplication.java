package com.hospital.triage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TriageSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriageSystemApplication.class, args);
    }
}
