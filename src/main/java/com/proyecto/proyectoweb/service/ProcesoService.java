package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.HistorialProceso;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.HistorialProcesoRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcesoService {

    @Autowired
    private ProcesoRepository procesoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistorialProcesoRepository historialRepository;

    @Autowired
    private ModelMapper modelMapper;

    
    @Transactional
    public ProcesoDTO crearProceso(ProcesoDTO procesoDTO) {
        Empresa empresa = empresaRepository.findById(procesoDTO.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        Proceso proceso = new Proceso();
        proceso.setNombre(procesoDTO.getNombre());
        proceso.setDescripcion(procesoDTO.getDescripcion());
        proceso.setCategoria(procesoDTO.getCategoria());
        proceso.setEmpresa(empresa);
        proceso.setStatus("ACTIVO");

        try {
            if (procesoDTO.getEstado() != null) {
                proceso.setEstado(Proceso.EstadoProceso.valueOf(procesoDTO.getEstado().toUpperCase()));
            } else {
                proceso.setEstado(Proceso.EstadoProceso.BORRADOR);
            }
        } catch (IllegalArgumentException e) {
            proceso.setEstado(Proceso.EstadoProceso.BORRADOR);
        }

        Proceso guardado = procesoRepository.save(proceso);
        return modelMapper.map(guardado, ProcesoDTO.class);
    }

    
    @Transactional
    public ProcesoDTO actualizarProceso(Long id, ProcesoDTO dto) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para validar permisos"));

        if (usuario.getRol() == Usuario.RolUsuario.SOLO_LECTURA) {
            throw new RuntimeException("Acceso denegado: Tu rol es Solo Lectura");
        }

        proceso.setNombre(dto.getNombre());
        proceso.setDescripcion(dto.getDescripcion());
        proceso.setCategoria(dto.getCategoria());
        
        if (dto.getEstado() != null) {
            try {
                proceso.setEstado(Proceso.EstadoProceso.valueOf(dto.getEstado().toUpperCase()));
            } catch (IllegalArgumentException e) {
                
            }
        }

        HistorialProceso historial = new HistorialProceso();
        historial.setProceso(proceso);
        historial.setUsuario(usuario);
        historial.setDescripcionCambio("Modificación de información básica del proceso");
        historialRepository.save(historial);

        Proceso actualizado = procesoRepository.save(proceso);
        return modelMapper.map(actualizado, ProcesoDTO.class);
    }

    
    @Transactional
    public void eliminarProceso(Long id, Long usuarioId) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() != Usuario.RolUsuario.ADMINISTRADOR) {
            throw new RuntimeException("Permisos insuficientes: Solo un Administrador puede eliminar procesos");
        }

        proceso.setEstado(Proceso.EstadoProceso.INACTIVO);
        procesoRepository.save(proceso);
        procesoRepository.delete(proceso);
    }

    
    public ProcesoDTO obtenerDetalleProceso(Long id) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        
        
        return modelMapper.map(proceso, ProcesoDTO.class);
    }

    public List<ProcesoDTO> listarConFiltros(Long empresaId, String estado, String categoria) {
        Proceso.EstadoProceso estadoEnum = null;
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                estadoEnum = Proceso.EstadoProceso.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                
            }
        }

        return procesoRepository.buscarConFiltros(empresaId, estadoEnum, categoria)
                .stream()
                .map(p -> modelMapper.map(p, ProcesoDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProcesoDTO> listarPorEmpresa(Long empresaId) {
        return procesoRepository.findByEmpresaId(empresaId).stream()
                .map(p -> modelMapper.map(p, ProcesoDTO.class))
                .collect(Collectors.toList());
    }
}