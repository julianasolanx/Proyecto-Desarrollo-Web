package com.proyecto.proyectoweb.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.UsuarioRepository;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

  
    @Transactional
    public EmpresaDTO registrarEmpresa(EmpresaDTO empresaDTO) {

        Empresa empresa = modelMapper.map(empresaDTO, Empresa.class);
        empresa.setStatus("ACTIVO");
        Empresa empresaGuardada = empresaRepository.save(empresa);

        Usuario admin = new Usuario();
        admin.setNombre("Admin " + empresaGuardada.getNombre());
        admin.setCorreo(empresaGuardada.getCorreoContacto());
        admin.setContrasena("123456"); 
        admin.setRol(Usuario.RolUsuario.ADMINISTRADOR);
        admin.setStatus("ACTIVO");
        admin.setEmpresa(empresaGuardada);
        
        usuarioRepository.save(admin);

        return modelMapper.map(empresaGuardada, EmpresaDTO.class);
    }

    
    public List<EmpresaDTO> listarEmpresas() {
        return empresaRepository.findAll().stream()
                .map(e -> modelMapper.map(e, EmpresaDTO.class))
                .collect(Collectors.toList());
    }

    public EmpresaDTO obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id).orElse(null);
        return (empresa != null) ? modelMapper.map(empresa, EmpresaDTO.class) : null;
    }
}