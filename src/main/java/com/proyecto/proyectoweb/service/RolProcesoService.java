package com.proyecto.proyectoweb.service;
import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.RolProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class RolProcesoService {
    private final RolProcesoRepository rolProcesoRepository;
    private final ModelMapper modelMapper;

    public RolProcesoService(RolProcesoRepository rolProcesoRepository, ModelMapper modelMapper) {
        this.rolProcesoRepository = rolProcesoRepository;
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
        RolProceso rolProceso = rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
        return modelMapper.map(rolProceso, RolProcesoDTO.class);
    }

    @Transactional
    public RolProcesoDTO crearRol(RolProcesoDTO dto) {
        RolProceso rolProceso = modelMapper.map(dto, RolProceso.class);
        RolProceso saved = rolProcesoRepository.save(rolProceso);
        return modelMapper.map(saved, RolProcesoDTO.class);
    }

    @Transactional
    public RolProcesoDTO actualizarRol(Long id, RolProcesoDTO dto) {
        RolProceso existing = rolProcesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
        modelMapper.map(dto, existing);
        RolProceso saved = rolProcesoRepository.save(existing);
        return modelMapper.map(saved, RolProcesoDTO.class);
    }

    @Transactional
    public void eliminarRol(Long id) {
        if (!rolProcesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado");
        }
        rolProcesoRepository.deleteById(id);
    }
}