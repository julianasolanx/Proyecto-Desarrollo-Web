package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.service.ProcesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/procesos")
@RequiredArgsConstructor
public class ProcesoController {

    private final ProcesoService procesoService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ProcesoDTO>> listar(@PathVariable Long empresaId) {
        return ResponseEntity.ok(procesoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcesoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(procesoService.obtenerProceso(id));
    }

    @PostMapping
    public ResponseEntity<ProcesoDTO> crear(@RequestBody ProcesoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procesoService.crearProceso(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcesoDTO> actualizar(@PathVariable Long id, @RequestBody ProcesoDTO dto) {
        return ResponseEntity.ok(procesoService.actualizarProceso(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        procesoService.eliminarProceso(id);
        return ResponseEntity.noContent().build();
    }
}