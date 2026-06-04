package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.LaneDTO;
import com.proyecto.proyectoweb.entity.Lane;
import com.proyecto.proyectoweb.entity.Pool;
import com.proyecto.proyectoweb.repository.LaneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaneService {

    private final LaneRepository laneRepository;
    private final PoolService poolService;
    private final ActividadService actividadService;

    public LaneService(LaneRepository laneRepository,
                       PoolService poolService,
                       @Lazy ActividadService actividadService) {
        this.laneRepository = laneRepository;
        this.poolService = poolService;
        this.actividadService = actividadService;
    }

    @Transactional(readOnly = true)
    public List<LaneDTO> listarPorPool(Long poolId) {
        return laneRepository.findByPoolId(poolId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LaneDTO> listarPorEmpresa(Long empresaId) {
        return laneRepository.findByPoolEmpresaId(empresaId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LaneDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public LaneDTO crear(LaneDTO dto) {
        Pool pool = poolService.obtenerEntidad(dto.getPoolId());
        Lane lane = new Lane();
        lane.setNombre(dto.getNombre());
        lane.setDescripcion(dto.getDescripcion());
        lane.setPool(pool);
        return toDTO(laneRepository.save(lane));
    }

    @Transactional
    public LaneDTO actualizar(Long id, LaneDTO dto) {
        Lane lane = findById(id);
        lane.setNombre(dto.getNombre());
        lane.setDescripcion(dto.getDescripcion());
        return toDTO(laneRepository.save(lane));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!laneRepository.existsById(id)) throw new EntityNotFoundException("Lane no encontrada: " + id);
        if (actividadService.existePorLane(id)) {
            throw new IllegalStateException("No se puede eliminar la lane porque tiene actividades asignadas. Reasigne primero.");
        }
        laneRepository.deleteById(id);
    }

    public Lane obtenerEntidad(Long id) {
        return findById(id);
    }

    private Lane findById(Long id) {
        return laneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lane no encontrada: " + id));
    }

    private LaneDTO toDTO(Lane lane) {
        LaneDTO dto = new LaneDTO();
        dto.setId(lane.getId());
        dto.setNombre(lane.getNombre());
        dto.setDescripcion(lane.getDescripcion());
        dto.setPoolId(lane.getPool().getId());
        return dto;
    }
}
