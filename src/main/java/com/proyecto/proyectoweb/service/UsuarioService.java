package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.CrearUsuarioDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          @Lazy EmpresaService empresaService,
                          EmailService emailService,
                          ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.empresaService = empresaService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        Type listType = new TypeToken<List<UsuarioDTO>>() {}.getType();
        return modelMapper.map(usuarios, listType);
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
    public UsuarioDTO crearUsuario(CrearUsuarioDTO dto) {
        Empresa empresa = empresaService.obtenerEntidad(dto.getEmpresaId());

        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setEmpresa(empresa);
        Usuario saved = usuarioRepository.save(usuario);

        emailService.enviarCorreo(
            dto.getCorreo(),
            "Invitación al sistema - " + empresa.getNombre(),
            "Has sido invitado a la plataforma. Tus credenciales son:\n" +
            "Correo: " + dto.getCorreo() + "\n" +
            "Contraseña: " + dto.getContrasena()
        );

        return modelMapper.map(saved, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        dto.setId(id);
        modelMapper.map(dto, usuario);

        if (dto.getEmpresaId() != null) {
            usuario.setEmpresa(empresaService.obtenerEntidad(dto.getEmpresaId()));
        }

        return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
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
        Usuario usuario = usuarioRepository.login(correo, contrasena)
                .orElseThrow(() -> new EntityNotFoundException("Credenciales incorrectas"));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }
}
