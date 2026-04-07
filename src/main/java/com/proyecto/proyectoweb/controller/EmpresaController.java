package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> listar() {
        return ResponseEntity.ok(empresaService.listarEmpresas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerEmpresa(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO> crear(@RequestBody EmpresaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.crearEmpresa(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> actualizar(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.actualizarEmpresa(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}