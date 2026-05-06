package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.HistorialCambioDTO;
import com.proyecto.proyectoweb.service.HistorialCambioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialCambioController {

    private final HistorialCambioService historialCambioService;

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<HistorialCambioDTO>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(historialCambioService.listarPorProceso(procesoId));
    }

    @GetMapping("/actividad/{actividadId}")
    public ResponseEntity<List<HistorialCambioDTO>> listarPorActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(historialCambioService.listarPorActividad(actividadId));
    }
}
