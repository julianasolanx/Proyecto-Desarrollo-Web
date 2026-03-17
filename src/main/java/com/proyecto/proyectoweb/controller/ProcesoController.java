package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.service.ProcesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proceso")
@CrossOrigin(origins = "*")
public class ProcesoController {

    @Autowired
    private ProcesoService procesoService;

   
    @PostMapping("/crear")
    public ResponseEntity<ProcesoDTO> crearProceso(@RequestBody ProcesoDTO procesoDTO) {
        try {
            ProcesoDTO nuevoProceso = procesoService.crearProceso(procesoDTO);
            return new ResponseEntity<>(nuevoProceso, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

   
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ProcesoDTO> actualizarProceso(@PathVariable Long id, @RequestBody ProcesoDTO dto) {
        try {
            ProcesoDTO actualizado = procesoService.actualizarProceso(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/eliminar/{id}/{usuarioId}")
    public ResponseEntity<String> eliminarProceso(@PathVariable Long id, @PathVariable Long usuarioId) {
        try {
            procesoService.eliminarProceso(id, usuarioId);
            return new ResponseEntity<>("Proceso inactivado correctamente.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar.");
        }
    }

    @GetMapping("/empresa/{empresaId}/buscar")
    public ResponseEntity<List<ProcesoDTO>> buscarProcesos(
            @PathVariable Long empresaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria) {
        
        List<ProcesoDTO> resultados = procesoService.listarConFiltros(empresaId, estado, categoria);
        return ResponseEntity.ok(resultados);
    }

    
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ProcesoDTO> verDetalle(@PathVariable Long id) {
        try {
            ProcesoDTO detalle = procesoService.obtenerDetalleProceso(id);
            return ResponseEntity.ok(detalle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ProcesoDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        List<ProcesoDTO> procesos = procesoService.listarPorEmpresa(empresaId);
        return new ResponseEntity<>(procesos, HttpStatus.OK);
    }
}