package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProcesoService {

    private final ProcesoRepository procesoRepository;
    private final EmpresaService empresaService;
    private final ActividadService actividadService;
    private final GatewayService gatewayService;
    private final ArcoService arcoService;
    private final ModelMapper modelMapper;

    public ProcesoService(ProcesoRepository procesoRepository,
                          EmpresaService empresaService,
                          @Lazy ActividadService actividadService,
                          @Lazy GatewayService gatewayService,
                          @Lazy ArcoService arcoService,
                          ModelMapper modelMapper) {
        this.procesoRepository = procesoRepository;
        this.empresaService = empresaService;
        this.actividadService = actividadService;
        this.gatewayService = gatewayService;
        this.arcoService = arcoService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<ProcesoDTO> listarProcesos() {
        List<Proceso> procesos = procesoRepository.findAll();
        Type listType = new TypeToken<List<ProcesoDTO>>() {}.getType();
        return modelMapper.map(procesos, listType);
    }

    @Transactional(readOnly = true)
    public List<ProcesoDTO> listarPorEmpresa(Long empresaId) {
        List<Proceso> procesos = procesoRepository.findByEmpresaId(empresaId);
        Type listType = new TypeToken<List<ProcesoDTO>>() {}.getType();
        return modelMapper.map(procesos, listType);
    }

    @Transactional(readOnly = true)
    public List<ProcesoDTO> listarPorEmpresaConFiltros(Long empresaId, String estado, String categoria) {
        List<Proceso> procesos = procesoRepository.findByEmpresaIdAndFiltros(empresaId, estado, categoria);
        Type listType = new TypeToken<List<ProcesoDTO>>() {}.getType();
        return modelMapper.map(procesos, listType);
    }

    @Transactional(readOnly = true)
    public ProcesoDTO obtenerProceso(Long id) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
        return modelMapper.map(proceso, ProcesoDTO.class);
    }

    public Proceso obtenerEntidad(Long id) {
        return procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
    }

    @Transactional
    public ProcesoDTO crearProceso(ProcesoDTO dto) {
        Empresa empresa = empresaService.obtenerEntidad(dto.getEmpresaId());

        Proceso proceso = modelMapper.map(dto, Proceso.class);
        proceso.setEmpresa(empresa);

        return modelMapper.map(procesoRepository.save(proceso), ProcesoDTO.class);
    }

    @Transactional
    public ProcesoDTO actualizarProceso(Long id, ProcesoDTO dto) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));

        dto.setId(id);
        modelMapper.map(dto, proceso);

        if (dto.getEmpresaId() != null) {
            proceso.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        }

        return modelMapper.map(procesoRepository.save(proceso), ProcesoDTO.class);
    }

    @Transactional
    public void eliminarProceso(Long id) {
        if (!procesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Proceso no encontrado");
        }
        if (actividadService.existenPorProceso(id) || gatewayService.existenPorProceso(id) || arcoService.existenArcosPorProceso(id)) {
            throw new IllegalStateException(
                "No se puede eliminar el proceso porque tiene elementos asociados. " +
                "Elimine primero los arcos, actividades y gateways del proceso.");
        }
        procesoRepository.deleteById(id);
    }
}
