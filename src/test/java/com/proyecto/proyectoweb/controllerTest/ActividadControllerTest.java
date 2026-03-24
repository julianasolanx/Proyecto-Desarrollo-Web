package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.ActividadController;
import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.service.ActividadService;
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
class ActividadControllerTest {

    @Mock
    private ActividadService actividadService;

    @InjectMocks
    private ActividadController actividadController;

    private ActividadDTO actividadDTO;
    private Long procesoId;
    private Long actividadId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        actividadId = 1L;
        actividadDTO = new ActividadDTO();
        actividadDTO.setId(actividadId);
        actividadDTO.setNombre("Test Actividad");
    }

    @Test
    void listar_Success() {
        List<ActividadDTO> expectedList = Arrays.asList(actividadDTO);
        when(actividadService.listarActividades()).thenReturn(expectedList);

        ResponseEntity<List<ActividadDTO>> response = actividadController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(actividadDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(actividadService).listarActividades();
    }

    @Test
    void listar_ListaVacia() {
        when(actividadService.listarActividades()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ActividadDTO>> response = actividadController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(actividadService).listarActividades();
    }

    @Test
    void listarPorProceso_Success() {
        List<ActividadDTO> expectedList = Arrays.asList(actividadDTO);
        when(actividadService.listarPorProceso(procesoId)).thenReturn(expectedList);

        ResponseEntity<List<ActividadDTO>> response = actividadController.listarPorProceso(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(actividadDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(actividadService).listarPorProceso(procesoId);
    }

    @Test
    void listarPorProceso_ListaVacia() {
        when(actividadService.listarPorProceso(procesoId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ActividadDTO>> response = actividadController.listarPorProceso(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(actividadService).listarPorProceso(procesoId);
    }

    @Test
    void obtener_Success() {
        when(actividadService.obtenerActividad(actividadId)).thenReturn(actividadDTO);

        ResponseEntity<ActividadDTO> response = actividadController.obtener(actividadId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(actividadDTO.getNombre(), response.getBody().getNombre());
        verify(actividadService).obtenerActividad(actividadId);
    }

    @Test
    void crear_Success() {
        when(actividadService.crearActividad(any(ActividadDTO.class))).thenReturn(actividadDTO);

        ResponseEntity<ActividadDTO> response = actividadController.crear(actividadDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(actividadDTO.getNombre(), response.getBody().getNombre());
        verify(actividadService).crearActividad(actividadDTO);
    }

    @Test
    void actualizar_Success() {
        when(actividadService.actualizarActividad(eq(actividadId), any(ActividadDTO.class))).thenReturn(actividadDTO);

        ResponseEntity<ActividadDTO> response = actividadController.actualizar(actividadId, actividadDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(actividadDTO.getNombre(), response.getBody().getNombre());
        verify(actividadService).actualizarActividad(actividadId, actividadDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(actividadService).eliminarActividad(actividadId);

        ResponseEntity<Void> response = actividadController.eliminar(actividadId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(actividadService).eliminarActividad(actividadId);
    }
}