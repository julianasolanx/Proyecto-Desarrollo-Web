package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.CrearUsuarioDTO;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Usuario;
import com.proyecto.proyectoweb.repository.UsuarioRepository;
import com.proyecto.proyectoweb.service.EmailService;
import com.proyecto.proyectoweb.service.EmpresaService;
import com.proyecto.proyectoweb.service.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private EmailService emailService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private CrearUsuarioDTO crearUsuarioDTO;
    private Empresa empresa;
    private Long empresaId;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        usuarioId = 1L;

        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Test Empresa");

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombre("Test Usuario");
        usuario.setCorreo("test@mail.com");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuarioId);
        usuarioDTO.setNombre("Test Usuario");
        usuarioDTO.setCorreo("test@mail.com");
        usuarioDTO.setEmpresaId(empresaId);

        crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setNombre("Test Usuario");
        crearUsuarioDTO.setCorreo("test@mail.com");
        crearUsuarioDTO.setContrasena("pass123");
        crearUsuarioDTO.setEmpresaId(empresaId);
    }

    @Test
    void listarUsuarios_Success() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        List<UsuarioDTO> expectedDTOs = Arrays.asList(usuarioDTO);
        Type listType = new TypeToken<List<UsuarioDTO>>() {}.getType();

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(modelMapper.map(usuarios, listType)).thenReturn(expectedDTOs);

        List<UsuarioDTO> result = usuarioService.listarUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void listarPorEmpresa_Success() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        List<UsuarioDTO> expectedDTOs = Arrays.asList(usuarioDTO);
        Type listType = new TypeToken<List<UsuarioDTO>>() {}.getType();

        when(usuarioRepository.findByEmpresaId(empresaId)).thenReturn(usuarios);
        when(modelMapper.map(usuarios, listType)).thenReturn(expectedDTOs);

        List<UsuarioDTO> result = usuarioService.listarPorEmpresa(empresaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuarioDTO.getNombre(), result.get(0).getNombre());
    }

    @Test
    void obtenerUsuario_Success() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.obtenerUsuario(usuarioId);

        assertNotNull(result);
        assertEquals(usuarioDTO.getNombre(), result.getNombre());
    }

    @Test
    void obtenerUsuario_NotFound() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            usuarioService.obtenerUsuario(usuarioId));
    }

    @Test
    void crearUsuario_Success() {
        when(empresaService.obtenerEntidad(empresaId)).thenReturn(empresa);
        when(modelMapper.map(crearUsuarioDTO, Usuario.class)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);
        doNothing().when(emailService).enviarCorreo(anyString(), anyString(), anyString());

        UsuarioDTO result = usuarioService.crearUsuario(crearUsuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getNombre(), result.getNombre());
        verify(usuarioRepository).save(usuario);
        verify(emailService).enviarCorreo(eq(crearUsuarioDTO.getCorreo()), anyString(), anyString());
    }

    @Test
    void crearUsuario_EmpresaNotFound() {
        when(empresaService.obtenerEntidad(empresaId)).thenThrow(new EntityNotFoundException("Empresa no encontrada"));

        assertThrows(EntityNotFoundException.class, () ->
            usuarioService.crearUsuario(crearUsuarioDTO));
    }

    @Test
    void actualizarUsuario_Success() {
        UsuarioDTO updateDTO = new UsuarioDTO();
        updateDTO.setNombre("Updated Usuario");
        updateDTO.setEmpresaId(empresaId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(empresaService.obtenerEntidad(empresaId)).thenReturn(empresa);
        doAnswer(invocation -> {
            UsuarioDTO source = invocation.getArgument(0);
            Usuario destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.actualizarUsuario(usuarioId, updateDTO);

        assertNotNull(result);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void login_Success() {
        when(usuarioRepository.login("test@mail.com", "pass123")).thenReturn(Optional.of(usuario));
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.login("test@mail.com", "pass123");

        assertNotNull(result);
        assertEquals(usuarioDTO.getNombre(), result.getNombre());
    }

    @Test
    void login_CredencialesIncorrectas() {
        when(usuarioRepository.login("bad@mail.com", "wrong")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            usuarioService.login("bad@mail.com", "wrong"));
    }

    @Test
    void actualizarUsuario_NullEmpresaId() {
        UsuarioDTO updateDTO = new UsuarioDTO();
        updateDTO.setNombre("Updated Usuario");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        doAnswer(invocation -> null).when(modelMapper).map(updateDTO, usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.actualizarUsuario(usuarioId, updateDTO);

        assertNotNull(result);
        verify(empresaService, never()).obtenerEntidad(any());
    }

    @Test
    void actualizarUsuario_NotFound() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            usuarioService.actualizarUsuario(usuarioId, usuarioDTO));
    }

    @Test
    void eliminarUsuario_Success() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(usuarioId);

        assertDoesNotThrow(() -> usuarioService.eliminarUsuario(usuarioId));
        verify(usuarioRepository).deleteById(usuarioId);
    }

    @Test
    void eliminarUsuario_NotFound() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            usuarioService.eliminarUsuario(usuarioId));
    }
}
