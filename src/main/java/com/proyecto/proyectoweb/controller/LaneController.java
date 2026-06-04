package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.LaneDTO;
import com.proyecto.proyectoweb.service.LaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lanes")
@RequiredArgsConstructor
public class LaneController {

    private final LaneService laneService;

    @GetMapping("/pool/{poolId}")
    public ResponseEntity<List<LaneDTO>> listarPorPool(@PathVariable Long poolId) {
        return ResponseEntity.ok(laneService.listarPorPool(poolId));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<LaneDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(laneService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaneDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(laneService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<LaneDTO> crear(@RequestBody LaneDTO dto) {
        return ResponseEntity.ok(laneService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaneDTO> actualizar(@PathVariable Long id, @RequestBody LaneDTO dto) {
        return ResponseEntity.ok(laneService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        laneService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
