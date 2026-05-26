package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.PoolDTO;
import com.proyecto.proyectoweb.service.PoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
public class PoolController {

    private final PoolService poolService;

    @GetMapping
    public ResponseEntity<List<PoolDTO>> listar() {
        return ResponseEntity.ok(poolService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoolDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(poolService.obtenerPorId(id));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<PoolDTO> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(poolService.obtenerPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<PoolDTO> crear(@RequestBody PoolDTO dto) {
        return ResponseEntity.ok(poolService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoolDTO> actualizar(@PathVariable Long id, @RequestBody PoolDTO dto) {
        return ResponseEntity.ok(poolService.actualizar(id, dto));
    }
}
