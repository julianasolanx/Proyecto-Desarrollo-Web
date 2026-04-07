package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.entity.Gateway;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.service.ActividadService;
import com.proyecto.proyectoweb.service.ArcoService;
import com.proyecto.proyectoweb.service.GatewayService;
import com.proyecto.proyectoweb.service.ProcesoService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArcoServiceTest {

    @Mock
    private ArcoRepository arcoRepository;

    @Mock
    private ProcesoService procesoService;

    @Mock
    private ActividadService actividadService;

    @Mock
    private GatewayService gatewayService;

    @InjectMocks
    private ArcoService arcoService;

    private Arco arco;
    private ArcoDTO arcoDTO;
    private Proceso proceso;
    private Long procesoId;
    private Long arcoId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        arcoId = 1L;

        proceso = new Proceso();
        proceso.setId(procesoId);

        arco = new Arco();
        arco.setId(arcoId);
        arco.setCondicion("condicion");
        arco.setProceso(proceso);

        arcoDTO = new ArcoDTO();
        arcoDTO.setId(arcoId);
        arcoDTO.setCondicion("condicion");
        arcoDTO.setProcesoId(procesoId);
    }

    @Test
    void listarArcos_Success() {
        List<Arco> arcos = Arrays.asList(arco);

        when(arcoRepository.findAll()).thenReturn(arcos);

        List<ArcoDTO> result = arcoService.listarArcos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(arcoRepository).findAll();
    }

    @Test
    void listarPorProceso_Success() {
        List<Arco> arcos = Arrays.asList(arco);

        when(arcoRepository.findByProcesoId(procesoId)).thenReturn(arcos);

        List<ArcoDTO> result = arcoService.listarPorProceso(procesoId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(arcoDTO.getCondicion(), result.get(0).getCondicion());
    }

    @Test
    void obtenerArco_Success() {
        when(arcoRepository.findById(arcoId)).thenReturn(Optional.of(arco));

        ArcoDTO result = arcoService.obtenerArco(arcoId);

        assertNotNull(result);
        assertEquals(arcoDTO.getCondicion(), result.getCondicion());
    }

    @Test
    void obtenerArco_NotFound() {
        when(arcoRepository.findById(arcoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.obtenerArco(arcoId));
    }

    @Test
    void crearArco_Success() {
        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        when(arcoRepository.save(any(Arco.class))).thenReturn(arco);

        ArcoDTO result = arcoService.crearArco(arcoDTO);

        assertNotNull(result);
        assertEquals(arcoDTO.getCondicion(), result.getCondicion());
        verify(arcoRepository).save(any(Arco.class));
    }

    @Test
    void crearArco_ProcesoNotFound() {
        when(procesoService.obtenerEntidad(procesoId)).thenThrow(new EntityNotFoundException("Proceso no encontrado"));

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.crearArco(arcoDTO));
    }

    @Test
    void actualizarArco_Success() {
        ArcoDTO updateDTO = new ArcoDTO();
        updateDTO.setCondicion("Updated condicion");
        updateDTO.setProcesoId(procesoId);

        when(arcoRepository.findById(arcoId)).thenReturn(Optional.of(arco));
        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        when(arcoRepository.save(arco)).thenReturn(arco);

        ArcoDTO result = arcoService.actualizarArco(arcoId, updateDTO);

        assertNotNull(result);
        verify(arcoRepository).findById(arcoId);
        verify(arcoRepository).save(arco);
    }

    @Test
    void actualizarArco_NotFound() {
        when(arcoRepository.findById(arcoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.actualizarArco(arcoId, arcoDTO));
    }

    @Test
    void eliminarArco_Success() {
        when(arcoRepository.existsById(arcoId)).thenReturn(true);
        doNothing().when(arcoRepository).deleteById(arcoId);

        assertDoesNotThrow(() -> arcoService.eliminarArco(arcoId));
        verify(arcoRepository).deleteById(arcoId);
    }

    @Test
    void crearArco_ConActividadOrigen() {
        Long actividadOrigenId = 2L;
        Actividad actividadOrigen = new Actividad();
        actividadOrigen.setId(actividadOrigenId);

        arcoDTO.setActividadOrigenId(actividadOrigenId);

        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        when(actividadService.obtenerEntidad(actividadOrigenId)).thenReturn(actividadOrigen);
        when(arcoRepository.save(any(Arco.class))).thenReturn(arco);

        ArcoDTO result = arcoService.crearArco(arcoDTO);

        assertNotNull(result);
        verify(actividadService).obtenerEntidad(actividadOrigenId);
    }

    @Test
    void crearArco_ConGatewayOrigen() {
        Long gatewayOrigenId = 3L;
        Gateway gateway = new Gateway();
        gateway.setId(gatewayOrigenId);

        arcoDTO.setGatewayOrigenId(gatewayOrigenId);

        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        when(gatewayService.obtenerEntidad(gatewayOrigenId)).thenReturn(gateway);
        when(arcoRepository.save(any(Arco.class))).thenReturn(arco);

        ArcoDTO result = arcoService.crearArco(arcoDTO);

        assertNotNull(result);
        verify(gatewayService).obtenerEntidad(gatewayOrigenId);
    }

    @Test
    void eliminarArco_NotFound() {
        when(arcoRepository.existsById(arcoId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.eliminarArco(arcoId));
    }
}
