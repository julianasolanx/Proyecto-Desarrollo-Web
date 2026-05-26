package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.ArcoController;
import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.service.ArcoService;
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
class ArcoControllerTest {

    @Mock
    private ArcoService arcoService;

    @InjectMocks
    private ArcoController arcoController;

    private ArcoDTO arcoDTO;
    private Long procesoId;
    private Long arcoId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        arcoId = 1L;
        arcoDTO = new ArcoDTO();
        arcoDTO.setId(arcoId);
        arcoDTO.setCondicion("Test Arco");
    }

    @Test
    void listar_Success() {
        List<ArcoDTO> expectedList = Arrays.asList(arcoDTO);
        when(arcoService.listarArcos()).thenReturn(expectedList);

        ResponseEntity<List<ArcoDTO>> response = arcoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(arcoDTO.getCondicion(), response.getBody().get(0).getCondicion());
        verify(arcoService).listarArcos();
    }

    @Test
    void listar_ListaVacia() {
        when(arcoService.listarArcos()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ArcoDTO>> response = arcoController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(arcoService).listarArcos();
    }


    @Test
    void listarPorProceso_Success() {
        List<ArcoDTO> expectedList = Arrays.asList(arcoDTO);
        when(arcoService.listarPorProceso(procesoId)).thenReturn(expectedList);

        ResponseEntity<List<ArcoDTO>> response = arcoController.listarPorProceso(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(arcoDTO.getCondicion(), response.getBody().get(0).getCondicion());
        verify(arcoService).listarPorProceso(procesoId);
    }

    @Test
    void listarPorProceso_ListaVacia() {
        when(arcoService.listarPorProceso(procesoId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ArcoDTO>> response = arcoController.listarPorProceso(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(arcoService).listarPorProceso(procesoId);
    }

    @Test
    void obtener_Success() {
        when(arcoService.obtenerArco(arcoId)).thenReturn(arcoDTO);

        ResponseEntity<ArcoDTO> response = arcoController.obtener(arcoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(arcoDTO.getCondicion(), response.getBody().getCondicion());
        verify(arcoService).obtenerArco(arcoId);
    }

    @Test
    void crear_Success() {
        when(arcoService.crearArco(any(ArcoDTO.class))).thenReturn(arcoDTO);

        ResponseEntity<ArcoDTO> response = arcoController.crear(arcoDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(arcoDTO.getCondicion(), response.getBody().getCondicion());
        verify(arcoService).crearArco(arcoDTO);
    }


    @Test
    void actualizar_Success() {
        when(arcoService.actualizarArco(eq(arcoId), any(ArcoDTO.class))).thenReturn(arcoDTO);

        ResponseEntity<ArcoDTO> response = arcoController.actualizar(arcoId, arcoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(arcoDTO.getCondicion(), response.getBody().getCondicion());
        verify(arcoService).actualizarArco(arcoId, arcoDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(arcoService).eliminarArco(arcoId);

        ResponseEntity<Void> response = arcoController.eliminar(arcoId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(arcoService).eliminarArco(arcoId);
    }
}