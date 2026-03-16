package com.proyecto.proyectoweb.service;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEmpresa(Long empresaId) {
        List<Usuario> usuarios = usuarioRepository.findByEmpresaId(empresaId);
        Type listType = new TypeToken<List<UsuarioDTO>>() {}.getType();
        return modelMapper.map(usuarios, listType);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        Usuario saved = usuarioRepository.save(usuario);
        return modelMapper.map(saved, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario existing = usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        modelMapper.map(dto, existing);
        Usuario saved = usuarioRepository.save(existing);
        return modelMapper.map(saved, UsuarioDTO.class);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}