package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.repository.ActividadRepository;
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
    private final ModelMapper modelMapper;

    public ActividadService(ActividadRepository actividadRepository, ModelMapper modelMapper) {
        this.actividadRepository = actividadRepository;
        this.modelMapper = modelMapper;
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
        Actividad actividad = modelMapper.map(dto, Actividad.class);
        Actividad saved = actividadRepository.save(actividad);
        return modelMapper.map(saved, ActividadDTO.class);
    }

    @Transactional
    public ActividadDTO actualizarActividad(Long id, ActividadDTO dto) {
        Actividad existing = actividadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));
        modelMapper.map(dto, existing);
        Actividad saved = actividadRepository.save(existing);
        return modelMapper.map(saved, ActividadDTO.class);
    }

    @Transactional
    public void eliminarActividad(Long id) {
        if (!actividadRepository.existsById(id)) {
            throw new EntityNotFoundException("Actividad no encontrada");
        }
        actividadRepository.deleteById(id);
    }
}