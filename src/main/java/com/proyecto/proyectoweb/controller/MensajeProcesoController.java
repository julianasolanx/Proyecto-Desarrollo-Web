package com.proyecto.proyectoweb.controller;

import com.proyecto.proyectoweb.dto.EventoMensajeDTO;
import com.proyecto.proyectoweb.dto.MensajeProcesoDTO;
import com.proyecto.proyectoweb.dto.ReglaCorrelacionDTO;
import com.proyecto.proyectoweb.service.MensajeProcesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeProcesoController {

    private final MensajeProcesoService mensajeService;

    // ─── MENSAJES ────────────────────────────────────────────────────────────

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<MensajeProcesoDTO>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(mensajeService.listarPorProceso(procesoId));
    }

    @PostMapping
    public ResponseEntity<MensajeProcesoDTO> crear(@RequestBody MensajeProcesoDTO dto) {
        return ResponseEntity.ok(mensajeService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensajeProcesoDTO> actualizar(@PathVariable Long id, @RequestBody MensajeProcesoDTO dto) {
        return ResponseEntity.ok(mensajeService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mensajeService.eliminarMensaje(id);
        return ResponseEntity.noContent().build();
    }

    // ─── THROW (HU-25) ───────────────────────────────────────────────────────

    @PostMapping("/{id}/lanzar")
    public ResponseEntity<EventoMensajeDTO> lanzar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String payload = body.get("payload");
        String businessKey = body.get("businessKey");
        return ResponseEntity.ok(mensajeService.lanzarMensaje(id, payload, businessKey));
    }

    // ─── EVENTOS / AUDITORÍA (HU-25 + HU-28) ────────────────────────────────

    @GetMapping("/eventos/proceso/{procesoId}")
    public ResponseEntity<List<EventoMensajeDTO>> listarEventos(@PathVariable Long procesoId) {
        return ResponseEntity.ok(mensajeService.listarEventosPorProceso(procesoId));
    }

    // ─── REGLAS DE CORRELACIÓN (HU-28) ───────────────────────────────────────

    @GetMapping("/reglas/proceso/{procesoId}")
    public ResponseEntity<List<ReglaCorrelacionDTO>> listarReglas(@PathVariable Long procesoId) {
        return ResponseEntity.ok(mensajeService.listarReglasPorProceso(procesoId));
    }

    @PostMapping("/reglas")
    public ResponseEntity<ReglaCorrelacionDTO> crearRegla(@RequestBody ReglaCorrelacionDTO dto) {
        return ResponseEntity.ok(mensajeService.crearRegla(dto));
    }

    @DeleteMapping("/reglas/{id}")
    public ResponseEntity<Void> eliminarRegla(@PathVariable Long id) {
        mensajeService.eliminarRegla(id);
        return ResponseEntity.noContent().build();
    }
}
