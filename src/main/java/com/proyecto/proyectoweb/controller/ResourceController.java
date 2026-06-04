package com.proyecto.proyectoweb.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ResourceController {

    // Público — no necesita token
    @GetMapping("/public/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    // Protegido — cualquier usuario autenticado
    @GetMapping("/api/profile")
    public Map<String, Object> profile(Authentication auth) {
        return Map.of(
                "user", auth.getName(),
                "authorities", auth.getAuthorities(),
                "message", "Token válido, acceso concedido");
    }

    // Protegido — solo ROLE_ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin")
    public Map<String, String> admin(Authentication auth) {
        return Map.of(
                "user", auth.getName(),
                "message", "Bienvenido, administrador");
    }
}
