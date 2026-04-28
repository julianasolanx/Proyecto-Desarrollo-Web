package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.HistorialCambioDTO;
import com.proyecto.proyectoweb.entity.HistorialCambio;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.HistorialCambioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class HistorialCambioService {

    private final HistorialCambioRepository historialCambioRepository;

    public HistorialCambioService(HistorialCambioRepository historialCambioRepository) {
        this.historialCambioRepository = historialCambioRepository;
    }

    @Transactional(readOnly = true)
    public List<HistorialCambioDTO> listarPorProceso(Long procesoId) {
        return historialCambioRepository
                .findByProcesoIdOrderByFechaCambioDesc(procesoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistorialCambioDTO> listarPorActividad(Long actividadId) {
        return historialCambioRepository
                .findByEntidadAndEntidadIdOrderByFechaCambioDesc("ACTIVIDAD", actividadId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void registrarSiCambio(String entidad, Long entidadId, String campo,
                                   String valorAnterior, String valorNuevo, Proceso proceso) {
        if (!Objects.equals(valorAnterior, valorNuevo)) {
            HistorialCambio historial = new HistorialCambio();
            historial.setEntidad(entidad);
            historial.setEntidadId(entidadId);
            historial.setCampo(campo);
            historial.setValorAnterior(valorAnterior);
            historial.setValorNuevo(valorNuevo);
            historial.setFechaCambio(LocalDateTime.now());
            historial.setProceso(proceso);
            historialCambioRepository.save(historial);
        }
    }

    private HistorialCambioDTO toDTO(HistorialCambio h) {
        HistorialCambioDTO dto = new HistorialCambioDTO();
        dto.setId(h.getId());
        dto.setEntidad(h.getEntidad());
        dto.setEntidadId(h.getEntidadId());
        dto.setCampo(h.getCampo());
        dto.setValorAnterior(h.getValorAnterior());
        dto.setValorNuevo(h.getValorNuevo());
        dto.setFechaCambio(h.getFechaCambio() != null ? h.getFechaCambio().toString() : null);
        dto.setProcesoId(h.getProceso() != null ? h.getProceso().getId() : null);
        return dto;
    }
}
