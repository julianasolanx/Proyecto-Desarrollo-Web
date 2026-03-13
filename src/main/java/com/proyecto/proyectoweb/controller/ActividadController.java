package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<ActividadDTO>> listar(@PathVariable Long procesoId) {
        return ResponseEntity.ok(actividadService.listarPorProceso(procesoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.obtenerActividad(id));
    }

    @PostMapping
    public ResponseEntity<ActividadDTO> crear(@RequestBody ActividadDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actividadService.crearActividad(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadDTO> actualizar(@PathVariable Long id, @RequestBody ActividadDTO dto) {
        return ResponseEntity.ok(actividadService.actualizarActividad(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }
}