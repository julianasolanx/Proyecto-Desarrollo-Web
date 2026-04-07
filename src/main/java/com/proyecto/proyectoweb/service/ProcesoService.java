package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.GatewayRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProcesoService {

    private final ProcesoRepository procesoRepository;
    private final EmpresaRepository empresaRepository;
    private final ActividadRepository actividadRepository;
    private final GatewayRepository gatewayRepository;
    private final ArcoRepository arcoRepository;
    private final ModelMapper modelMapper;

    public ProcesoService(ProcesoRepository procesoRepository,
                          EmpresaRepository empresaRepository,
                          ActividadRepository actividadRepository,
                          GatewayRepository gatewayRepository,
                          ArcoRepository arcoRepository,
                          ModelMapper modelMapper) {
        this.procesoRepository = procesoRepository;
        this.empresaRepository = empresaRepository;
        this.actividadRepository = actividadRepository;
        this.gatewayRepository = gatewayRepository;
        this.arcoRepository = arcoRepository;
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

    @Transactional
    public ProcesoDTO crearProceso(ProcesoDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));

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
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
            proceso.setEmpresa(empresa);
        }

        return modelMapper.map(procesoRepository.save(proceso), ProcesoDTO.class);
    }

    @Transactional
    public void eliminarProceso(Long id) {
        if (!procesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Proceso no encontrado");
        }
        boolean tieneActividades = !actividadRepository.findByProcesoId(id).isEmpty();
        boolean tieneGateways = !gatewayRepository.findByProcesoId(id).isEmpty();
        boolean tieneArcos = !arcoRepository.findByProcesoId(id).isEmpty();
        if (tieneActividades || tieneGateways || tieneArcos) {
            throw new IllegalStateException(
                "No se puede eliminar el proceso porque tiene elementos asociados. " +
                "Elimine primero los arcos, actividades y gateways del proceso.");
        }
        procesoRepository.deleteById(id);
    }
}
