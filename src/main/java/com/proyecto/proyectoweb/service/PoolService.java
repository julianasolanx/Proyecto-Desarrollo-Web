package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.PoolDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Pool;
import com.proyecto.proyectoweb.repository.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolService {

    private final PoolRepository poolRepository;
    private final EmpresaService empresaService;
    private final HistorialCambioService historialCambioService;

    @Transactional(readOnly = true)
    public List<PoolDTO> listarTodos() {
        return poolRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PoolDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public PoolDTO obtenerPorEmpresa(Long empresaId) {
        return poolRepository.findByEmpresaId(empresaId)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pool no encontrado para empresa: " + empresaId));
    }

    @Transactional
    public PoolDTO crear(PoolDTO dto) {
        Empresa empresa = empresaService.obtenerEntidad(dto.getEmpresaId());
        if (poolRepository.existsByEmpresaId(empresa.getId())) {
            throw new IllegalStateException("La empresa ya tiene un pool asignado.");
        }
        Pool pool = new Pool();
        pool.setNombre(dto.getNombre());
        pool.setDescripcion(dto.getDescripcion());
        pool.setEmpresa(empresa);
        Pool saved = poolRepository.save(pool);
        historialCambioService.registrarCambio("POOL", saved.getId(), "creacion", null, saved.getNombre(), null);
        return toDTO(saved);
    }

    @Transactional
    public PoolDTO actualizar(Long id, PoolDTO dto) {
        Pool pool = findById(id);
        String nombreAnterior = pool.getNombre();
        pool.setNombre(dto.getNombre());
        pool.setDescripcion(dto.getDescripcion());
        Pool saved = poolRepository.save(pool);
        historialCambioService.registrarSiCambio("POOL", id, "nombre", nombreAnterior, dto.getNombre(), null);
        return toDTO(saved);
    }

    public Pool obtenerEntidad(Long id) {
        return findById(id);
    }

    public Pool obtenerEntidadPorEmpresa(Long empresaId) {
        return poolRepository.findByEmpresaId(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Pool no encontrado para empresa: " + empresaId));
    }

    /** Crea el pool por defecto al registrar una empresa nueva (HU-21) */
    @Transactional
    public Pool crearPoolParaEmpresa(Empresa empresa) {
        Pool pool = new Pool();
        pool.setNombre("Pool de " + empresa.getNombre());
        pool.setDescripcion("Pool principal de la empresa " + empresa.getNombre());
        pool.setEmpresa(empresa);
        return poolRepository.save(pool);
    }

    private Pool findById(Long id) {
        return poolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pool no encontrado: " + id));
    }

    private PoolDTO toDTO(Pool pool) {
        PoolDTO dto = new PoolDTO();
        dto.setId(pool.getId());
        dto.setNombre(pool.getNombre());
        dto.setDescripcion(pool.getDescripcion());
        dto.setEmpresaId(pool.getEmpresa().getId());
        dto.setEmpresaNombre(pool.getEmpresa().getNombre());
        return dto;
    }
}
