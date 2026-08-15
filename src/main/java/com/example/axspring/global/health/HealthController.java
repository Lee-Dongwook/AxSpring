package com.example.axspring.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health") 
    public String health() {
            return "OK";
    }
    
    @GetMapping("/error-test")
    public String errorTest() {
        throw new RuntimeException("boom");
    }
}
