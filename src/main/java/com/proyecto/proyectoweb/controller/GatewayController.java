package com.proyecto.proyectoweb.controller;
import com.proyecto.proyectoweb.dto.GatewayDTO;
import com.proyecto.proyectoweb.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gateways")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @GetMapping
    public ResponseEntity<List<GatewayDTO>> listar() {
        return ResponseEntity.ok(gatewayService.listarGateways());
    }

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<GatewayDTO>> listarPorProceso(@PathVariable Long procesoId) {
        return ResponseEntity.ok(gatewayService.listarPorProceso(procesoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatewayDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(gatewayService.obtenerGateway(id));
    }

    @PostMapping
    public ResponseEntity<GatewayDTO> crear(@RequestBody GatewayDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gatewayService.crearGateway(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GatewayDTO> actualizar(@PathVariable Long id, @RequestBody GatewayDTO dto) {
        return ResponseEntity.ok(gatewayService.actualizarGateway(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gatewayService.eliminarGateway(id);
        return ResponseEntity.noContent().build();
    }
}