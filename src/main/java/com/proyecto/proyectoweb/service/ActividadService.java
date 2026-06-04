package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final ArcoService arcoService;
    private final ProcesoService procesoService;
    private final RolProcesoService rolProcesoService;
    private final HistorialCambioService historialCambioService;
    private final ModelMapper modelMapper;

    public ActividadService(ActividadRepository actividadRepository,
                            @Lazy ArcoService arcoService,
                            ProcesoService procesoService,
                            RolProcesoService rolProcesoService,
                            HistorialCambioService historialCambioService,
                            ModelMapper modelMapper) {
        this.actividadRepository = actividadRepository;
        this.arcoService = arcoService;
        this.procesoService = procesoService;
        this.rolProcesoService = rolProcesoService;
        this.historialCambioService = historialCambioService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<ActividadDTO> listarActividades() {
        List<Actividad> actividades = actividadRepository.findAll();
        Type listType = new TypeToken<List<ActividadDTO>>() {}.getType();
        return modelMapper.map(actividades, listType);
    }

    @Transactional(readOnly = true)
    public List<ActividadDTO> listarPorProceso(Long procesoId) {
        List<Actividad> actividades = actividadRepository.findByProcesoId(procesoId);
        Type listType = new TypeToken<List<ActividadDTO>>() {}.getType();
        return modelMapper.map(actividades, listType);
    }

    @Transactional(readOnly = true)
    public ActividadDTO obtenerActividad(Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));
        return modelMapper.map(actividad, ActividadDTO.class);
    }

    public Actividad obtenerEntidad(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));
    }

    public boolean existenPorProceso(Long procesoId) {
        return actividadRepository.existsByProcesoId(procesoId);
    }

    public boolean existePorRolResponsable(Long rolId) {
        return actividadRepository.existsByRolResponsableId(rolId);
    }

    public boolean existePorLane(Long laneId) {
        return actividadRepository.existsByLaneId(laneId);
    }

    @Transactional
    public ActividadDTO crearActividad(ActividadDTO dto) {
        Actividad actividad = modelMapper.map(dto, Actividad.class);
        actividad.setProceso(procesoService.obtenerEntidad(dto.getProcesoId()));
        actividad.setRolResponsable(resolverRol(dto.getRolResponsableId()));

        return modelMapper.map(actividadRepository.save(actividad), ActividadDTO.class);
    }

    @Transactional
    public ActividadDTO actualizarActividad(Long id, ActividadDTO dto) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));

        String nombreAnterior = actividad.getNombre();
        String descAnterior = actividad.getDescripcion();
        String tipoAnterior = actividad.getTipo() != null ? actividad.getTipo().name() : null;
        String rolAnterior = actividad.getRolResponsable() != null
                ? actividad.getRolResponsable().getId().toString() : null;
        Proceso proceso = actividad.getProceso();

        dto.setId(id);
        modelMapper.map(dto, actividad);

        if (dto.getProcesoId() != null) {
            actividad.setProceso(procesoService.obtenerEntidad(dto.getProcesoId()));
        }
        actividad.setRolResponsable(resolverRol(dto.getRolResponsableId()));

        Actividad saved = actividadRepository.save(actividad);

        String rolNuevo = dto.getRolResponsableId() != null ? dto.getRolResponsableId().toString() : null;
        historialCambioService.registrarSiCambio("ACTIVIDAD", id, "nombre", nombreAnterior, dto.getNombre(), proceso);
        historialCambioService.registrarSiCambio("ACTIVIDAD", id, "descripcion", descAnterior, dto.getDescripcion(), proceso);
        historialCambioService.registrarSiCambio("ACTIVIDAD", id, "tipo", tipoAnterior, dto.getTipo(), proceso);
        historialCambioService.registrarSiCambio("ACTIVIDAD", id, "rolResponsable", rolAnterior, rolNuevo, proceso);

        return modelMapper.map(saved, ActividadDTO.class);
    }

    @Transactional
    public void eliminarActividad(Long id) {
        if (!actividadRepository.existsById(id)) {
            throw new EntityNotFoundException("Actividad no encontrada");
        }
        if (arcoService.existenArcosPorActividad(id)) {
            throw new IllegalStateException("No se puede eliminar la actividad porque tiene arcos conectados. Elimine primero los arcos asociados.");
        }
        actividadRepository.deleteById(id);
    }

    private RolProceso resolverRol(Long rolId) {
        if (rolId == null) return null;
        return rolProcesoService.obtenerEntidad(rolId);
    }
}
