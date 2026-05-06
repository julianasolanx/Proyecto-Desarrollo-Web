package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.CrearUsuarioDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          @Lazy EmpresaService empresaService,
                          ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.empresaService = empresaService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuario(Long id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado")));
    }

    @Transactional
    public UsuarioDTO crearUsuario(CrearUsuarioDTO dto) {
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado");
        }

        Usuario usuario = modelMapper.map(dto, Usuario.class);

        if (dto.getEmpresaId() != null) {
            usuario.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        }

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        dto.setId(id);
        modelMapper.map(dto, usuario);

        // empresaId removido del DTO; la empresa no se modifica al actualizar usuario

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public void registrarAdmin(Empresa empresa) {
        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setCorreo(empresa.getCorreoContacto());
        admin.setContrasena("admin123");
        admin.setRol(Usuario.RolUsuario.ADMINISTRADOR);
        admin.setEmpresa(empresa);
        usuarioRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO login(String correo, String contrasena) {
        return toDTO(usuarioRepository.login(correo, contrasena)
                .orElseThrow(() -> new EntityNotFoundException("Credenciales incorrectas")));
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
        if (usuario.getEmpresa() != null) {
            dto.setEmpresaId(usuario.getEmpresa().getId());
        }
        return dto;
    }



    @Transactional
    public Map<String, Object> invitarUsuario(CrearUsuarioDTO dto) {
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado");
        }

        String contrasenaGenerada = generarContrasenaAleatoria();

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario_" + UUID.randomUUID().toString().substring(0, 6));
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(contrasenaGenerada);
        usuario.setRol(Usuario.RolUsuario.valueOf(dto.getRol()));

        if (dto.getEmpresaId() != null) {
            usuario.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        }

        UsuarioDTO usuarioDTO = toDTO(usuarioRepository.save(usuario));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("usuario", usuarioDTO);
        resultado.put("contrasenaTemp", contrasenaGenerada);
        return resultado;
    }

    private String generarContrasenaAleatoria() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
