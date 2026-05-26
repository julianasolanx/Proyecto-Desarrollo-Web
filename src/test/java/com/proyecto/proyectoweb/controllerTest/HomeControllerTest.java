package com.proyecto.proyectoweb.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.proyecto.proyectoweb.controller.HomeController;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    void home_Success() {
        ResponseEntity<Map<String, Object>> response = homeController.home();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("Editor de Procesos Empresariales", body.get("app"));
        assertEquals("1.0.0", body.get("version"));
        assertEquals("running", body.get("status"));
        
        Map<String, String> endpoints = (Map<String, String>) body.get("endpoints");
        assertNotNull(endpoints);
        assertEquals("/api/empresas", endpoints.get("empresas"));
        assertEquals("/api/usuarios", endpoints.get("usuarios"));
        assertEquals("/api/procesos", endpoints.get("procesos"));
        assertEquals("/api/actividades", endpoints.get("actividades"));
        assertEquals("/api/gateways", endpoints.get("gateways"));
        assertEquals("/api/arcos", endpoints.get("arcos"));
        assertEquals("/api/roles", endpoints.get("roles"));
    }
}