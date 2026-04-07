package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.entity.Gateway;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.GatewayRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArcoService {

    private final ArcoRepository arcoRepository;
    private final ProcesoRepository procesoRepository;
    private final ActividadRepository actividadRepository;
    private final GatewayRepository gatewayRepository;
    public ArcoService(ArcoRepository arcoRepository,
                       ProcesoRepository procesoRepository,
                       ActividadRepository actividadRepository,
                       GatewayRepository gatewayRepository,
                       ModelMapper modelMapper) {
        this.arcoRepository = arcoRepository;
        this.procesoRepository = procesoRepository;
        this.actividadRepository = actividadRepository;
        this.gatewayRepository = gatewayRepository;
    }

    @Transactional(readOnly = true)
    public List<ArcoDTO> listarArcos() {
        return arcoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArcoDTO> listarPorProceso(Long procesoId) {
        return arcoRepository.findByProcesoId(procesoId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArcoDTO obtenerArco(Long id) {
        Arco arco = arcoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arco no encontrado"));
        return mapToDTO(arco);
    }

    @Transactional
    public ArcoDTO crearArco(ArcoDTO dto) {
        Arco arco = new Arco();
        arco.setCondicion(dto.getCondicion());
        arco.setStatus(0);
        resolverRelaciones(arco, dto);
        return mapToDTO(arcoRepository.save(arco));
    }

    @Transactional
    public ArcoDTO actualizarArco(Long id, ArcoDTO dto) {
        Arco arco = arcoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arco no encontrado"));
        arco.setCondicion(dto.getCondicion());
        resolverRelaciones(arco, dto);
        return mapToDTO(arcoRepository.save(arco));
    }

    @Transactional
    public void eliminarArco(Long id) {
        if (!arcoRepository.existsById(id)) {
            throw new EntityNotFoundException("Arco no encontrado");
        }
        arcoRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Métodos privados de apoyo
    // -------------------------------------------------------

    private void resolverRelaciones(Arco arco, ArcoDTO dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
        arco.setProceso(proceso);

        if (dto.getActividadOrigenId() != null) {
            Actividad origen = actividadRepository.findById(dto.getActividadOrigenId())
                    .orElseThrow(() -> new EntityNotFoundException("Actividad origen no encontrada"));
            arco.setActividadOrigen(origen);
        } else {
            arco.setActividadOrigen(null);
        }

        if (dto.getGatewayOrigenId() != null) {
            Gateway gOrigen = gatewayRepository.findById(dto.getGatewayOrigenId())
                    .orElseThrow(() -> new EntityNotFoundException("Gateway origen no encontrado"));
            arco.setGatewayOrigen(gOrigen);
        } else {
            arco.setGatewayOrigen(null);
        }

        if (dto.getActividadDestinoId() != null) {
            Actividad destino = actividadRepository.findById(dto.getActividadDestinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Actividad destino no encontrada"));
            arco.setActividadDestino(destino);
        } else {
            arco.setActividadDestino(null);
        }

        if (dto.getGatewayDestinoId() != null) {
            Gateway gDestino = gatewayRepository.findById(dto.getGatewayDestinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Gateway destino no encontrado"));
            arco.setGatewayDestino(gDestino);
        } else {
            arco.setGatewayDestino(null);
        }
    }

    private ArcoDTO mapToDTO(Arco arco) {
        ArcoDTO dto = new ArcoDTO();
        dto.setId(arco.getId());
        dto.setCondicion(arco.getCondicion());
        dto.setProcesoId(arco.getProceso() != null ? arco.getProceso().getId() : null);
        dto.setActividadOrigenId(arco.getActividadOrigen() != null ? arco.getActividadOrigen().getId() : null);
        dto.setGatewayOrigenId(arco.getGatewayOrigen() != null ? arco.getGatewayOrigen().getId() : null);
        dto.setActividadDestinoId(arco.getActividadDestino() != null ? arco.getActividadDestino().getId() : null);
        dto.setGatewayDestinoId(arco.getGatewayDestino() != null ? arco.getGatewayDestino().getId() : null);
        return dto;
    }
}