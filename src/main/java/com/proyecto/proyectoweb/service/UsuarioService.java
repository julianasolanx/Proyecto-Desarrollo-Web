package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.LoginDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ModelMapper modelMapper;

   
    @Transactional
    public UsuarioDTO registrarUsuarioEnEmpresa(UsuarioDTO usuarioDTO) {

        Empresa empresa = empresaRepository.findById(usuarioDTO.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Error: La empresa con ID " + usuarioDTO.getEmpresaId() + " no existe."));

        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(usuarioDTO.getNombre());
        nuevoUsuario.setCorreo(usuarioDTO.getCorreo());
        nuevoUsuario.setContrasena(usuarioDTO.getContrasena()); 
        nuevoUsuario.setStatus("ACTIVO");
        nuevoUsuario.setEmpresa(empresa);

        try {
            if (usuarioDTO.getRol() != null) {
                nuevoUsuario.setRol(Usuario.RolUsuario.valueOf(usuarioDTO.getRol().toUpperCase()));
            } else {
                nuevoUsuario.setRol(Usuario.RolUsuario.SOLO_LECTURA);
            }
        } catch (IllegalArgumentException e) {
        
            nuevoUsuario.setRol(Usuario.RolUsuario.SOLO_LECTURA);
        }

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

      
        return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
    }

   
    public UsuarioDTO login(LoginDTO loginDTO) {
       
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(loginDTO.getCorreo());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (usuario.getContrasena().equals(loginDTO.getContrasena())) {
                
                UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
            
                if (usuario.getEmpresa() != null) {
                    dto.setEmpresaId(usuario.getEmpresa().getId());
                }
                
                return dto;
            }
        }
        
        
        throw new RuntimeException("Credenciales inválidas: correo o contraseña incorrectos.");
    }
}