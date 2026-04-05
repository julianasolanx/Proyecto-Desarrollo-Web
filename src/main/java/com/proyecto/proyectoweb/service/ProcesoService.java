package com.proyecto.proyectoweb.service;
import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Proceso;
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
    private final ModelMapper modelMapper;

    public ProcesoService(ProcesoRepository procesoRepository, ModelMapper modelMapper) {
        this.procesoRepository = procesoRepository;
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
    public ProcesoDTO obtenerProceso(Long id) {
        Proceso proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
        return modelMapper.map(proceso, ProcesoDTO.class);
    }

    @Transactional
    public ProcesoDTO crearProceso(ProcesoDTO dto) {
        Proceso proceso = modelMapper.map(dto, Proceso.class);
        Proceso saved = procesoRepository.save(proceso);
        return modelMapper.map(saved, ProcesoDTO.class);
    }

    @Transactional
    public ProcesoDTO actualizarProceso(Long id, ProcesoDTO dto) {
        Proceso existing = procesoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
        modelMapper.map(dto, existing);
        Proceso saved = procesoRepository.save(existing);
        return modelMapper.map(saved, ProcesoDTO.class);
    }

    @Transactional
    public void eliminarProceso(Long id) {
        if (!procesoRepository.existsById(id)) {
            throw new EntityNotFoundException("Proceso no encontrado");
        }
        procesoRepository.deleteById(id);
    }
}