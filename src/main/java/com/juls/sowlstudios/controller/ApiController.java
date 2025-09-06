package com.juls.sowlstudios.controller;

import com.juls.sowlstudios.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@CrossOrigin(origins = {"${app.cors.allowed-origins}"})
public class ApiController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Photography Booking API");
        health.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Photography Booking System");
        info.put("version", "1.0.0");
        info.put("description", "API for managing photography booking appointments");
        info.put("contact", "admin@photography.com");

        return ResponseEntity.ok(ApiResponse.success("Application information", info));
    }
}
