package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.GatewayRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import com.proyecto.proyectoweb.service.ArcoService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    private ProcesoRepository procesoRepository;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private ModelMapper modelMapper;

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
        when(procesoRepository.findById(procesoId)).thenReturn(Optional.of(proceso));
        when(arcoRepository.save(any(Arco.class))).thenReturn(arco);

        ArcoDTO result = arcoService.crearArco(arcoDTO);

        assertNotNull(result);
        assertEquals(arcoDTO.getCondicion(), result.getCondicion());
        verify(arcoRepository).save(any(Arco.class));
    }

    @Test
    void crearArco_ProcesoNotFound() {
        when(procesoRepository.findById(procesoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.crearArco(arcoDTO));
    }

    @Test
    void actualizarArco_Success() {
        ArcoDTO updateDTO = new ArcoDTO();
        updateDTO.setCondicion("Updated condicion");
        updateDTO.setProcesoId(procesoId);

        when(arcoRepository.findById(arcoId)).thenReturn(Optional.of(arco));
        when(procesoRepository.findById(procesoId)).thenReturn(Optional.of(proceso));
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
    void eliminarArco_NotFound() {
        when(arcoRepository.existsById(arcoId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            arcoService.eliminarArco(arcoId));
    }
}
