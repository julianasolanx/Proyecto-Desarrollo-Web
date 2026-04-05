package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.UsuarioController;
import com.proyecto.proyectoweb.dto.UsuarioDTO;
import com.proyecto.proyectoweb.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private Long empresaId;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        usuarioId = 1L;
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuarioId);
        usuarioDTO.setNombre("Test Usuario");
    }

    @Test
    void listar_Success() {
        List<UsuarioDTO> expectedList = Arrays.asList(usuarioDTO);
        when(usuarioService.listarUsuarios()).thenReturn(expectedList);

        ResponseEntity<List<UsuarioDTO>> response = usuarioController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(usuarioDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(usuarioService).listarUsuarios();
    }

    @Test
    void listar_ListaVacia() {
        when(usuarioService.listarUsuarios()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsuarioDTO>> response = usuarioController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(usuarioService).listarUsuarios();
    }

    @Test
    void listarPorEmpresa_Success() {
        List<UsuarioDTO> expectedList = Arrays.asList(usuarioDTO);
        when(usuarioService.listarPorEmpresa(empresaId)).thenReturn(expectedList);

        ResponseEntity<List<UsuarioDTO>> response = usuarioController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(usuarioDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(usuarioService).listarPorEmpresa(empresaId);
    }

    @Test
    void listarPorEmpresa_ListaVacia() {
        when(usuarioService.listarPorEmpresa(empresaId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsuarioDTO>> response = usuarioController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(usuarioService).listarPorEmpresa(empresaId);
    }

    @Test
    void obtener_Success() {
        when(usuarioService.obtenerUsuario(usuarioId)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.obtener(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioDTO.getNombre(), response.getBody().getNombre());
        verify(usuarioService).obtenerUsuario(usuarioId);
    }

    @Test
    void crear_Success() {
        when(usuarioService.crearUsuario(any(UsuarioDTO.class))).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.crear(usuarioDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioDTO.getNombre(), response.getBody().getNombre());
        verify(usuarioService).crearUsuario(usuarioDTO);
    }

    @Test
    void actualizar_Success() {
        when(usuarioService.actualizarUsuario(eq(usuarioId), any(UsuarioDTO.class))).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.actualizar(usuarioId, usuarioDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioDTO.getNombre(), response.getBody().getNombre());
        verify(usuarioService).actualizarUsuario(usuarioId, usuarioDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(usuarioService).eliminarUsuario(usuarioId);

        ResponseEntity<Void> response = usuarioController.eliminar(usuarioId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(usuarioService).eliminarUsuario(usuarioId);
    }
}