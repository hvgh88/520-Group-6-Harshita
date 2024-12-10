package com.umass.hangout.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public String home() {
        return "Spring Boot application is running!";
    }
}
