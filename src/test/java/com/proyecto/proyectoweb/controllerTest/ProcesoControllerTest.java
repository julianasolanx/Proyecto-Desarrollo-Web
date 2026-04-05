package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.ProcesoController;
import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.service.ProcesoService;
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
class ProcesoControllerTest {

    @Mock
    private ProcesoService procesoService;

    @InjectMocks
    private ProcesoController procesoController;

    private ProcesoDTO procesoDTO;
    private Long empresaId;
    private Long procesoId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        procesoId = 1L;
        procesoDTO = new ProcesoDTO();
        procesoDTO.setId(procesoId);
        procesoDTO.setNombre("Test Proceso");
    }

    @Test
    void listar_Success() {
        List<ProcesoDTO> expectedList = Arrays.asList(procesoDTO);
        when(procesoService.listarProcesos()).thenReturn(expectedList);

        ResponseEntity<List<ProcesoDTO>> response = procesoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(procesoDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(procesoService).listarProcesos();
    }

    @Test
    void listar_ListaVacia() {
        when(procesoService.listarProcesos()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProcesoDTO>> response = procesoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(procesoService).listarProcesos();
    }

    @Test
    void listarPorEmpresa_Success() {
        List<ProcesoDTO> expectedList = Arrays.asList(procesoDTO);
        when(procesoService.listarPorEmpresa(empresaId)).thenReturn(expectedList);

        ResponseEntity<List<ProcesoDTO>> response = procesoController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(procesoDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(procesoService).listarPorEmpresa(empresaId);
    }

    @Test
    void listarPorEmpresa_ListaVacia() {
        when(procesoService.listarPorEmpresa(empresaId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProcesoDTO>> response = procesoController.listarPorEmpresa(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(procesoService).listarPorEmpresa(empresaId);
    }

    @Test
    void obtener_Success() {
        when(procesoService.obtenerProceso(procesoId)).thenReturn(procesoDTO);

        ResponseEntity<ProcesoDTO> response = procesoController.obtener(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(procesoDTO.getNombre(), response.getBody().getNombre());
        verify(procesoService).obtenerProceso(procesoId);
    }

    @Test
    void crear_Success() {
        when(procesoService.crearProceso(any(ProcesoDTO.class))).thenReturn(procesoDTO);

        ResponseEntity<ProcesoDTO> response = procesoController.crear(procesoDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(procesoDTO.getNombre(), response.getBody().getNombre());
        verify(procesoService).crearProceso(procesoDTO);
    }

    @Test
    void actualizar_Success() {
        when(procesoService.actualizarProceso(eq(procesoId), any(ProcesoDTO.class))).thenReturn(procesoDTO);

        ResponseEntity<ProcesoDTO> response = procesoController.actualizar(procesoId, procesoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(procesoDTO.getNombre(), response.getBody().getNombre());
        verify(procesoService).actualizarProceso(procesoId, procesoDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(procesoService).eliminarProceso(procesoId);

        ResponseEntity<Void> response = procesoController.eliminar(procesoId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(procesoService).eliminarProceso(procesoId);
    }
}