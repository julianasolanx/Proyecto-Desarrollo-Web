package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class ArcoService {

    private final ArcoRepository arcoRepository;
    private final ModelMapper modelMapper;

    public ArcoService(ArcoRepository arcoRepository, ModelMapper modelMapper) {
        this.arcoRepository = arcoRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<ArcoDTO> listarPorProceso(Long procesoId) {
        List<Arco> arcos = arcoRepository.findByProcesoId(procesoId);
        Type listType = new TypeToken<List<ArcoDTO>>() {}.getType();
        return modelMapper.map(arcos, listType);
    }

    @Transactional(readOnly = true)
    public ArcoDTO obtenerArco(Long id) {
        Arco arco = arcoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Arco no encontrado"));
        return modelMapper.map(arco, ArcoDTO.class);
    }

    @Transactional
    public ArcoDTO crearArco(ArcoDTO dto) {
        Arco arco = modelMapper.map(dto, Arco.class);
        Arco saved = arcoRepository.save(arco);
        return modelMapper.map(saved, ArcoDTO.class);
    }

    @Transactional
    public ArcoDTO actualizarArco(Long id, ArcoDTO dto) {
        Arco existing = arcoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Arco no encontrado"));
        modelMapper.map(dto, existing);
        Arco saved = arcoRepository.save(existing);
        return modelMapper.map(saved, ArcoDTO.class);
    }

    @Transactional
    public void eliminarArco(Long id) {
        if (!arcoRepository.existsById(id)) {
            throw new EntityNotFoundException("Arco no encontrado");
        }
        arcoRepository.deleteById(id);
    }
}