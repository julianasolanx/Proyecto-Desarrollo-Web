package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.service.RolProcesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolProcesoController {

    private final RolProcesoService rolProcesoService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<RolProcesoDTO>> listar(@PathVariable Long empresaId) {
        return ResponseEntity.ok(rolProcesoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolProcesoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rolProcesoService.obtenerRol(id));
    }

    @PostMapping
    public ResponseEntity<RolProcesoDTO> crear(@RequestBody RolProcesoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolProcesoService.crearRol(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolProcesoDTO> actualizar(@PathVariable Long id, @RequestBody RolProcesoDTO dto) {
        return ResponseEntity.ok(rolProcesoService.actualizarRol(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rolProcesoService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}