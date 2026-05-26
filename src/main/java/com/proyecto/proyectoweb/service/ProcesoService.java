package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Pool;
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
    private final HistorialCambioService historialCambioService;
    private final PoolService poolService;
    private final ModelMapper modelMapper;

    public ProcesoService(ProcesoRepository procesoRepository,
                          EmpresaService empresaService,
                          @Lazy ActividadService actividadService,
                          @Lazy GatewayService gatewayService,
                          @Lazy ArcoService arcoService,
                          HistorialCambioService historialCambioService,
                          @Lazy PoolService poolService,
                          ModelMapper modelMapper) {
        this.procesoRepository = procesoRepository;
        this.empresaService = empresaService;
        this.actividadService = actividadService;
        this.gatewayService = gatewayService;
        this.arcoService = arcoService;
        this.historialCambioService = historialCambioService;
        this.poolService = poolService;
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
        return procesoRepository.findByEmpresaId(empresaId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProcesoDTO> listarPorEmpresaConFiltros(Long empresaId, String estado, String categoria) {
        return procesoRepository.findByEmpresaIdAndFiltros(empresaId, estado, categoria)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProcesoDTO obtenerProceso(Long id) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
        return toDTO(proceso);
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
        proceso.setEsCompartido(dto.getEsCompartido() != null ? dto.getEsCompartido() : false);

        // Asignar al pool de la empresa (HU-21)
        Pool pool = poolService.obtenerEntidadPorEmpresa(empresa.getId());
        proceso.setPool(pool);

        return toDTO(procesoRepository.save(proceso));
    }

    @Transactional
    public ProcesoDTO actualizarProceso(Long id, ProcesoDTO dto) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));

        String nombreAnterior = proceso.getNombre();
        String descAnterior = proceso.getDescripcion();
        String catAnterior = proceso.getCategoria();
        String estadoAnterior = proceso.getEstado() != null ? proceso.getEstado().name() : null;
        String compartidoAnterior = proceso.getEsCompartido() != null ? proceso.getEsCompartido().toString() : "false";

        dto.setId(id);
        modelMapper.map(dto, proceso);

        if (dto.getEmpresaId() != null) {
            Empresa empresa = empresaService.obtenerEntidad(dto.getEmpresaId());
            // Validar que el pool pertenece a la misma empresa (HU-21)
            if (proceso.getPool() != null && !proceso.getPool().getEmpresa().getId().equals(empresa.getId())) {
                throw new IllegalStateException("No se puede reasignar un proceso al pool de otra empresa.");
            }
            proceso.setEmpresa(empresa);
        }

        if (dto.getEsCompartido() != null) {
            proceso.setEsCompartido(dto.getEsCompartido());
        }

        Proceso saved = procesoRepository.save(proceso);

        historialCambioService.registrarSiCambio("PROCESO", id, "nombre", nombreAnterior, dto.getNombre(), saved);
        historialCambioService.registrarSiCambio("PROCESO", id, "descripcion", descAnterior, dto.getDescripcion(), saved);
        historialCambioService.registrarSiCambio("PROCESO", id, "categoria", catAnterior, dto.getCategoria(), saved);
        historialCambioService.registrarSiCambio("PROCESO", id, "estado", estadoAnterior, dto.getEstado(), saved);
        historialCambioService.registrarSiCambio("PROCESO", id, "esCompartido", compartidoAnterior,
                saved.getEsCompartido() != null ? saved.getEsCompartido().toString() : "false", saved);

        return toDTO(saved);
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

    private ProcesoDTO toDTO(Proceso p) {
        ProcesoDTO dto = new ProcesoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setCategoria(p.getCategoria());
        dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
        dto.setEmpresaId(p.getEmpresa() != null ? p.getEmpresa().getId() : null);
        dto.setEsCompartido(p.getEsCompartido() != null ? p.getEsCompartido() : false);
        dto.setPoolId(p.getPool() != null ? p.getPool().getId() : null);
        return dto;
    }
}
