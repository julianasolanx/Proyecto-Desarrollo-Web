package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.RolProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolProcesoService {

    private final RolProcesoRepository rolProcesoRepository;
    private final EmpresaService empresaService;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    public RolProcesoService(RolProcesoRepository rolProcesoRepository,
                             EmpresaService empresaService,
                             @Lazy ActividadService actividadService,
                             UsuarioService usuarioService,
                             ModelMapper modelMapper) {
        this.rolProcesoRepository = rolProcesoRepository;
        this.empresaService = empresaService;
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<RolProcesoDTO> listarRoles() {
        return rolProcesoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<RolProcesoDTO> listarPorEmpresa(Long empresaId) {
        return rolProcesoRepository.findByEmpresaId(empresaId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public RolProcesoDTO obtenerRol(Long id) {
        return toDTO(rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado")));
    }

    public RolProceso obtenerEntidad(Long id) {
        return rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
    }

    @Transactional
    public RolProcesoDTO crearRol(RolProcesoDTO dto) {
        RolProceso rol = modelMapper.map(dto, RolProceso.class);
        rol.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        return toDTO(rolProcesoRepository.save(rol));
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

        return toDTO(rolProcesoRepository.save(rol));
    }

    @Transactional
    public void eliminarRol(Long id) {
        if (!rolProcesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado");
        }
        if (actividadService.existePorRolResponsable(id)) {
            throw new IllegalStateException("No se puede eliminar el rol porque está asignado a actividades. Reasigne primero.");
        }
        long usuariosConRol = usuarioService.contarPorRolProceso(id);
        if (usuariosConRol > 0) {
            throw new IllegalStateException("No se puede eliminar el rol porque hay " + usuariosConRol +
                    " usuario(s) asignado(s). Reasigne primero.");
        }
        rolProcesoRepository.deleteById(id);
    }

    private RolProcesoDTO toDTO(RolProceso rol) {
        RolProcesoDTO dto = modelMapper.map(rol, RolProcesoDTO.class);
        if (rol.getActividades() != null) {
            dto.setActividadIds(rol.getActividades().stream()
                    .map(a -> a.getId())
                    .toList());
        }
        return dto;
    }
}
