package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.ProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.GatewayRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import com.proyecto.proyectoweb.service.ProcesoService;

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
class ProcesoServiceTest {

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private ArcoRepository arcoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProcesoService procesoService;

    private Proceso proceso;
    private ProcesoDTO procesoDTO;
    private Empresa empresa;
    private Long empresaId;
    private Long procesoId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        procesoId = 1L;

        empresa = new Empresa();
        empresa.setId(empresaId);

        proceso = new Proceso();
        proceso.setId(procesoId);
        proceso.setNombre("Test Proceso");
        proceso.setDescripcion("Descripción del proceso");

        procesoDTO = new ProcesoDTO();
        procesoDTO.setId(procesoId);
        procesoDTO.setNombre("Test Proceso");
        procesoDTO.setDescripcion("Descripción del proceso");
        procesoDTO.setEmpresaId(empresaId);
    }

    @Test
    void listarPorEmpresa_Success() {
        List<Proceso> procesos = Arrays.asList(proceso);
        List<ProcesoDTO> expectedDTOs = Arrays.asList(procesoDTO);
        Type listType = new TypeToken<List<ProcesoDTO>>() {}.getType();

        when(procesoRepository.findByEmpresaId(empresaId)).thenReturn(procesos);
        when(modelMapper.map(procesos, listType)).thenReturn(expectedDTOs);

        List<ProcesoDTO> result = procesoService.listarPorEmpresa(empresaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(procesoDTO.getNombre(), result.get(0).getNombre());
    }

    @Test
    void obtenerProceso_Success() {
        when(procesoRepository.findById(procesoId)).thenReturn(Optional.of(proceso));
        when(modelMapper.map(proceso, ProcesoDTO.class)).thenReturn(procesoDTO);

        ProcesoDTO result = procesoService.obtenerProceso(procesoId);

        assertNotNull(result);
        assertEquals(procesoDTO.getNombre(), result.getNombre());
    }

    @Test
    void obtenerProceso_NotFound() {
        when(procesoRepository.findById(procesoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            procesoService.obtenerProceso(procesoId));
    }

    @Test
    void crearProceso_Success() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(modelMapper.map(procesoDTO, Proceso.class)).thenReturn(proceso);
        when(procesoRepository.save(proceso)).thenReturn(proceso);
        when(modelMapper.map(proceso, ProcesoDTO.class)).thenReturn(procesoDTO);

        ProcesoDTO result = procesoService.crearProceso(procesoDTO);

        assertNotNull(result);
        assertEquals(procesoDTO.getNombre(), result.getNombre());
    }

    @Test
    void actualizarProceso_Success() {
        ProcesoDTO updateDTO = new ProcesoDTO();
        updateDTO.setNombre("Updated Proceso");

        when(procesoRepository.findById(procesoId)).thenReturn(Optional.of(proceso));
        doAnswer(invocation -> {
            ProcesoDTO source = invocation.getArgument(0);
            Proceso destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, proceso);
        when(procesoRepository.save(proceso)).thenReturn(proceso);
        when(modelMapper.map(proceso, ProcesoDTO.class)).thenReturn(procesoDTO);

        ProcesoDTO result = procesoService.actualizarProceso(procesoId, updateDTO);

        assertNotNull(result);
        verify(procesoRepository).findById(procesoId);
        verify(procesoRepository).save(proceso);
    }

    @Test
    void eliminarProceso_Success() {
        when(procesoRepository.existsById(procesoId)).thenReturn(true);
        when(actividadRepository.findByProcesoId(procesoId)).thenReturn(List.of());
        when(gatewayRepository.findByProcesoId(procesoId)).thenReturn(List.of());
        when(arcoRepository.findByProcesoId(procesoId)).thenReturn(List.of());
        doNothing().when(procesoRepository).deleteById(procesoId);

        assertDoesNotThrow(() -> procesoService.eliminarProceso(procesoId));
        verify(procesoRepository).deleteById(procesoId);
    }

    @Test
    void eliminarProceso_ConElementos_ThrowsConflict() {
        when(procesoRepository.existsById(procesoId)).thenReturn(true);
        when(actividadRepository.findByProcesoId(procesoId)).thenReturn(List.of(new com.proyecto.proyectoweb.entity.Actividad()));

        assertThrows(IllegalStateException.class, () ->
            procesoService.eliminarProceso(procesoId));
        verify(procesoRepository, never()).deleteById(any());
    }

    @Test
    void eliminarProceso_NotFound() {
        when(procesoRepository.existsById(procesoId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            procesoService.eliminarProceso(procesoId));
    }
}