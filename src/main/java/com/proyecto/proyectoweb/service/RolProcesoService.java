package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.RolProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RolProcesoService {

    private final RolProcesoRepository rolProcesoRepository;
    private final EmpresaService empresaService;
    private final ActividadService actividadService;
    private final ModelMapper modelMapper;

    public RolProcesoService(RolProcesoRepository rolProcesoRepository,
                             EmpresaService empresaService,
                             @Lazy ActividadService actividadService,
                             ModelMapper modelMapper) {
        this.rolProcesoRepository = rolProcesoRepository;
        this.empresaService = empresaService;
        this.actividadService = actividadService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<RolProcesoDTO> listarRoles() {
        List<RolProceso> roles = rolProcesoRepository.findAll();
        Type listType = new TypeToken<List<RolProcesoDTO>>() {}.getType();
        return modelMapper.map(roles, listType);
    }

    @Transactional(readOnly = true)
    public List<RolProcesoDTO> listarPorEmpresa(Long empresaId) {
        List<RolProceso> roles = rolProcesoRepository.findByEmpresaId(empresaId);
        Type listType = new TypeToken<List<RolProcesoDTO>>() {}.getType();
        return modelMapper.map(roles, listType);
    }

    @Transactional(readOnly = true)
    public RolProcesoDTO obtenerRol(Long id) {
        RolProceso rol = rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
        return modelMapper.map(rol, RolProcesoDTO.class);
    }

    public RolProceso obtenerEntidad(Long id) {
        return rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
    }

    @Transactional
    public RolProcesoDTO crearRol(RolProcesoDTO dto) {
        RolProceso rol = modelMapper.map(dto, RolProceso.class);
        rol.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));

        return modelMapper.map(rolProcesoRepository.save(rol), RolProcesoDTO.class);
    }

    @Transactional
    public RolProcesoDTO actualizarRol(Long id, RolProcesoDTO dto) {
        RolProceso rol = rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));

        dto.setId(id);
        modelMapper.map(dto, rol);

        if (dto.getEmpresaId() != null) {
            rol.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        }

        return modelMapper.map(rolProcesoRepository.save(rol), RolProcesoDTO.class);
    }

    @Transactional
    public void eliminarRol(Long id) {
        if (!rolProcesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado");
        }
        if (actividadService.existePorRolResponsable(id)) {
            throw new IllegalStateException("No se puede eliminar el rol porque está asignado a una o más actividades");
        }
        rolProcesoRepository.deleteById(id);
    }
}
