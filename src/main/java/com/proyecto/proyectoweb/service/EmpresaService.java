package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public EmpresaService(EmpresaRepository empresaRepository,
                          UsuarioRepository usuarioRepository,
                          ModelMapper modelMapper) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarEmpresas() {
        List<Empresa> empresas = empresaRepository.findAll();
        Type listType = new TypeToken<List<EmpresaDTO>>() {}.getType();
        return modelMapper.map(empresas, listType);
    }

    @Transactional(readOnly = true)
    public EmpresaDTO obtenerEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
        return modelMapper.map(empresa, EmpresaDTO.class);
    }

    @Transactional
    public EmpresaDTO crearEmpresa(EmpresaDTO dto) {
        Empresa empresa = modelMapper.map(dto, Empresa.class);
        Empresa saved = empresaRepository.save(empresa);

        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setCorreo(dto.getCorreoContacto());
        admin.setContrasena("admin123");
        admin.setRol(Usuario.RolUsuario.ADMINISTRADOR);
        admin.setEmpresa(saved);
        usuarioRepository.save(admin);

        return modelMapper.map(saved, EmpresaDTO.class);
    }

    @Transactional
    public EmpresaDTO actualizarEmpresa(Long id, EmpresaDTO dto) {
        Empresa existing = empresaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada"));
        dto.setId(id);
        modelMapper.map(dto, existing);
        Empresa saved = empresaRepository.save(existing);
        return modelMapper.map(saved, EmpresaDTO.class);
    }

    @Transactional
    public void eliminarEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new EntityNotFoundException("Empresa no encontrada");
        }
        empresaRepository.deleteById(id);
    }
}