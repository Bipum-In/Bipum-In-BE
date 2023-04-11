package com.sparta.bipuminbe.common.healthcheck.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @GetMapping("/actuator/health")
    public Map<String, Object> healthCheck(){
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");

        return result;
    }
}
