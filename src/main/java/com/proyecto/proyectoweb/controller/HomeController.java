package com.proyecto.proyectoweb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("app", "Editor de Procesos Empresariales");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "empresas", "/api/empresas",
            "usuarios", "/api/usuarios",
            "procesos", "/api/procesos",
            "actividades", "/api/actividades",
            "gateways", "/api/gateways",
            "arcos", "/api/arcos",
            "roles", "/api/roles"
        ));
        return ResponseEntity.ok(response);
    }
}