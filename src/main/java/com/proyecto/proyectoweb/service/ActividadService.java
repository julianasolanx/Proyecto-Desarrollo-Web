package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import com.proyecto.proyectoweb.repository.RolProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final ArcoRepository arcoRepository;
    private final ProcesoRepository procesoRepository;
    private final RolProcesoRepository rolProcesoRepository;
    private final ModelMapper modelMapper;

    public ActividadService(ActividadRepository actividadRepository,
                            ArcoRepository arcoRepository,
                            ProcesoRepository procesoRepository,
                            RolProcesoRepository rolProcesoRepository,
                            ModelMapper modelMapper) {
        this.actividadRepository = actividadRepository;
        this.arcoRepository = arcoRepository;
        this.procesoRepository = procesoRepository;
        this.rolProcesoRepository = rolProcesoRepository;
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

    @Transactional
    public ActividadDTO crearActividad(ActividadDTO dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));

        Actividad actividad = modelMapper.map(dto, Actividad.class);
        actividad.setProceso(proceso);
        actividad.setRolResponsable(resolverRol(dto.getRolResponsableId()));

        return modelMapper.map(actividadRepository.save(actividad), ActividadDTO.class);
    }

    @Transactional
    public ActividadDTO actualizarActividad(Long id, ActividadDTO dto) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));

        dto.setId(id);
        modelMapper.map(dto, actividad);

        if (dto.getProcesoId() != null) {
            Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                    .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
            actividad.setProceso(proceso);
        }
        actividad.setRolResponsable(resolverRol(dto.getRolResponsableId()));

        return modelMapper.map(actividadRepository.save(actividad), ActividadDTO.class);
    }

    @Transactional
    public void eliminarActividad(Long id) {
        if (!actividadRepository.existsById(id)) {
            throw new EntityNotFoundException("Actividad no encontrada");
        }
        boolean tieneArcos = !arcoRepository.findByActividadOrigenId(id).isEmpty()
                || !arcoRepository.findByActividadDestinoId(id).isEmpty();
        if (tieneArcos) {
            throw new IllegalStateException("No se puede eliminar la actividad porque tiene arcos conectados. Elimine primero los arcos asociados.");
        }
        actividadRepository.deleteById(id);
    }

    private RolProceso resolverRol(Long rolId) {
        if (rolId == null) return null;
        return rolProcesoRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
    }
}
