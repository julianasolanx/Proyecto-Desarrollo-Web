package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArcoService {

    private final ArcoRepository arcoRepository;
    private final ProcesoService procesoService;
    private final ActividadService actividadService;
    private final GatewayService gatewayService;

    public ArcoService(ArcoRepository arcoRepository,
                       ProcesoService procesoService,
                       ActividadService actividadService,
                       GatewayService gatewayService) {
        this.arcoRepository = arcoRepository;
        this.procesoService = procesoService;
        this.actividadService = actividadService;
        this.gatewayService = gatewayService;
    }

    @Transactional(readOnly = true)
    public List<ArcoDTO> listarArcos() {
        return arcoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArcoDTO> listarPorProceso(Long procesoId) {
        return arcoRepository.findByProcesoId(procesoId).stream()
                .map(this::mapToDTO)
                .toList();
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

    public boolean existenArcosPorActividad(Long actividadId) {
        return !arcoRepository.findByActividadOrigenId(actividadId).isEmpty()
                || !arcoRepository.findByActividadDestinoId(actividadId).isEmpty();
    }

    public boolean existenArcosPorGateway(Long gatewayId) {
        return !arcoRepository.findByGatewayOrigenId(gatewayId).isEmpty()
                || !arcoRepository.findByGatewayDestinoId(gatewayId).isEmpty();
    }

    public boolean existenArcosPorProceso(Long procesoId) {
        return !arcoRepository.findByProcesoId(procesoId).isEmpty();
    }

    // -------------------------------------------------------
    // Métodos privados de apoyo
    // -------------------------------------------------------

    private void resolverRelaciones(Arco arco, ArcoDTO dto) {
        arco.setProceso(procesoService.obtenerEntidad(dto.getProcesoId()));

        arco.setActividadOrigen(dto.getActividadOrigenId() != null
                ? actividadService.obtenerEntidad(dto.getActividadOrigenId()) : null);

        arco.setGatewayOrigen(dto.getGatewayOrigenId() != null
                ? gatewayService.obtenerEntidad(dto.getGatewayOrigenId()) : null);

        arco.setActividadDestino(dto.getActividadDestinoId() != null
                ? actividadService.obtenerEntidad(dto.getActividadDestinoId()) : null);

        arco.setGatewayDestino(dto.getGatewayDestinoId() != null
                ? gatewayService.obtenerEntidad(dto.getGatewayDestinoId()) : null);
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