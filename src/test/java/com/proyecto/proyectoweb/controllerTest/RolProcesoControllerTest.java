package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.RolProcesoController;
import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.service.RolProcesoService;
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
class RolProcesoControllerTest {

    @Mock
    private RolProcesoService rolProcesoService;

    @InjectMocks
    private RolProcesoController rolProcesoController;

    private RolProcesoDTO rolProcesoDTO;
    private Long empresaId;
    private Long rolId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        rolId = 1L;
        rolProcesoDTO = new RolProcesoDTO();
        rolProcesoDTO.setId(rolId);
        rolProcesoDTO.setNombre("Test Rol");
    }

    @Test
    void listar_Success() {
        List<RolProcesoDTO> expectedList = Arrays.asList(rolProcesoDTO);
        when(rolProcesoService.listarRoles()).thenReturn(expectedList);

        ResponseEntity<List<RolProcesoDTO>> response = rolProcesoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(rolProcesoDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(rolProcesoService).listarRoles();
    }

    @Test
    void listar_ListaVacia() {
        when(rolProcesoService.listarRoles()).thenReturn(Collections.emptyList());

        ResponseEntity<List<RolProcesoDTO>> response = rolProcesoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(rolProcesoService).listarRoles();
    }

    @Test
    void listarPorEmpresa_Success() {
        List<RolProcesoDTO> expectedList = Arrays.asList(rolProcesoDTO);
        when(rolProcesoService.listarPorEmpresa(empresaId)).thenReturn(expectedList);

        ResponseEntity<List<RolProcesoDTO>> response = rolProcesoController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(rolProcesoDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(rolProcesoService).listarPorEmpresa(empresaId);
    }

    @Test
    void listarPorEmpresa_ListaVacia() {
        when(rolProcesoService.listarPorEmpresa(empresaId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<RolProcesoDTO>> response = rolProcesoController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(rolProcesoService).listarPorEmpresa(empresaId);
    }

    @Test
    void obtener_Success() {
        when(rolProcesoService.obtenerRol(rolId)).thenReturn(rolProcesoDTO);

        ResponseEntity<RolProcesoDTO> response = rolProcesoController.obtener(rolId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rolProcesoDTO.getNombre(), response.getBody().getNombre());
        verify(rolProcesoService).obtenerRol(rolId);
    }

    @Test
    void crear_Success() {
        when(rolProcesoService.crearRol(any(RolProcesoDTO.class))).thenReturn(rolProcesoDTO);

        ResponseEntity<RolProcesoDTO> response = rolProcesoController.crear(rolProcesoDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rolProcesoDTO.getNombre(), response.getBody().getNombre());
        verify(rolProcesoService).crearRol(rolProcesoDTO);
    }

    @Test
    void actualizar_Success() {
        when(rolProcesoService.actualizarRol(eq(rolId), any(RolProcesoDTO.class))).thenReturn(rolProcesoDTO);

        ResponseEntity<RolProcesoDTO> response = rolProcesoController.actualizar(rolId, rolProcesoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rolProcesoDTO.getNombre(), response.getBody().getNombre());
        verify(rolProcesoService).actualizarRol(rolId, rolProcesoDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(rolProcesoService).eliminarRol(rolId);

        ResponseEntity<Void> response = rolProcesoController.eliminar(rolId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolProcesoService).eliminarRol(rolId);
    }
}