package com.proyecto.proyectoweb.controller;
import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.service.ArcoService;
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
@RequestMapping("/api/arcos")
@RequiredArgsConstructor
public class ArcoController {
    private final ArcoService arcoService;

    @GetMapping
    public ResponseEntity<List<ArcoDTO>> listar() {
        return ResponseEntity.ok(arcoService.listarArcos());
    }

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<ArcoDTO>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(arcoService.listarPorProceso(procesoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArcoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(arcoService.obtenerArco(id));
    }

    @PostMapping
    public ResponseEntity<ArcoDTO> crear(@RequestBody ArcoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(arcoService.crearArco(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArcoDTO> actualizar(@PathVariable Long id, @RequestBody ArcoDTO dto) {
        return ResponseEntity.ok(arcoService.actualizarArco(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        arcoService.eliminarArco(id);
        return ResponseEntity.noContent().build();
    }
}