package com.proyecto.proyectoweb.controllerTest;

import com.proyecto.proyectoweb.controller.EmpresaController;
import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.service.EmpresaService;
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
class EmpresaControllerTest {

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EmpresaController empresaController;

    private EmpresaDTO empresaDTO;
    private Long empresaId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        
        empresaDTO = new EmpresaDTO();
        empresaDTO.setId(empresaId);
        empresaDTO.setNombre("Test Empresa");
    }

    @Test
    void listar_Success() {
        List<EmpresaDTO> expectedList = Arrays.asList(empresaDTO);
        when(empresaService.listarEmpresas()).thenReturn(expectedList);

        ResponseEntity<List<EmpresaDTO>> response = empresaController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(empresaDTO.getNombre(), response.getBody().get(0).getNombre());
        verify(empresaService).listarEmpresas();
    }

    @Test
    void obtener_Success() {
        when(empresaService.obtenerEmpresa(empresaId)).thenReturn(empresaDTO);

        ResponseEntity<EmpresaDTO> response = empresaController.obtener(empresaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(empresaDTO.getNombre(), response.getBody().getNombre());
        verify(empresaService).obtenerEmpresa(empresaId);
    }

    @Test
    void crear_Success() {
        when(empresaService.crearEmpresa(any(EmpresaDTO.class))).thenReturn(empresaDTO);

        ResponseEntity<EmpresaDTO> response = empresaController.crear(empresaDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(empresaDTO.getNombre(), response.getBody().getNombre());
        verify(empresaService).crearEmpresa(empresaDTO);
    }

    @Test
    void actualizar_Success() {
        when(empresaService.actualizarEmpresa(eq(empresaId), any(EmpresaDTO.class))).thenReturn(empresaDTO);

        ResponseEntity<EmpresaDTO> response = empresaController.actualizar(empresaId, empresaDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(empresaDTO.getNombre(), response.getBody().getNombre());
        verify(empresaService).actualizarEmpresa(empresaId, empresaDTO);
    }

    @Test
    void eliminar_Success() {
        doNothing().when(empresaService).eliminarEmpresa(empresaId);

        ResponseEntity<Void> response = empresaController.eliminar(empresaId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(empresaService).eliminarEmpresa(empresaId);
    }
}