package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.ProcesoAccesoDTO;
import com.proyecto.proyectoweb.service.ProcesoAccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proceso-accesos")
@RequiredArgsConstructor
public class ProcesoAccesoController {

    private final ProcesoAccesoService accesoService;

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<ProcesoAccesoDTO>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(accesoService.listarPorProceso(procesoId));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ProcesoAccesoDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(accesoService.listarPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<ProcesoAccesoDTO> otorgar(@RequestBody ProcesoAccesoDTO dto) {
        return ResponseEntity.ok(accesoService.otorgarAcceso(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revocar(@PathVariable Long id) {
        accesoService.revocarAcceso(id);
        return ResponseEntity.noContent().build();
    }
}
