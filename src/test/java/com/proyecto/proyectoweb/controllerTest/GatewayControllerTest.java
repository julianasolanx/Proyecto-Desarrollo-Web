package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.GatewayController;
import com.proyecto.proyectoweb.dto.GatewayDTO;
import com.proyecto.proyectoweb.service.GatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayControllerTest {

    @Mock
    private GatewayService gatewayService;

    @InjectMocks
    private GatewayController gatewayController;

    private GatewayDTO gatewayDTO;
    private Long procesoId;
    private Long gatewayId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        gatewayId = 1L;
        
        gatewayDTO = new GatewayDTO();
        gatewayDTO.setId(gatewayId);
        gatewayDTO.setNombre("Test Gateway");
    }

    @Test
    void listar_Success() {
        List<GatewayDTO> expectedList = Arrays.asList(gatewayDTO);
        when(gatewayService.listarPorProceso(procesoId)).thenReturn(expectedList);

        ResponseEntity<List<GatewayDTO>> response = gatewayController.listar(procesoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(gatewayDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(gatewayService).listarPorProceso(procesoId);
    }

    @Test
    void obtener_Success() {
        when(gatewayService.obtenerGateway(gatewayId)).thenReturn(gatewayDTO);

        ResponseEntity<GatewayDTO> response = gatewayController.obtener(gatewayId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(gatewayDTO.getNombre(), response.getBody().getNombre());
        verify(gatewayService).obtenerGateway(gatewayId);
    }

    @Test
    void crear_Success() {
        when(gatewayService.crearGateway(any(GatewayDTO.class))).thenReturn(gatewayDTO);

        ResponseEntity<GatewayDTO> response = gatewayController.crear(gatewayDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(gatewayDTO.getNombre(), response.getBody().getNombre());
        verify(gatewayService).crearGateway(gatewayDTO);
    }

    @Test
    void actualizar_Success() {
        when(gatewayService.actualizarGateway(eq(gatewayId), any(GatewayDTO.class))).thenReturn(gatewayDTO);

        ResponseEntity<GatewayDTO> response = gatewayController.actualizar(gatewayId, gatewayDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(gatewayDTO.getNombre(), response.getBody().getNombre());
        verify(gatewayService).actualizarGateway(gatewayId, gatewayDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(gatewayService).eliminarGateway(gatewayId);

        ResponseEntity<Void> response = gatewayController.eliminar(gatewayId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(gatewayService).eliminarGateway(gatewayId);
    }
}