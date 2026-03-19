package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.ActividadDTO;
import com.proyecto.proyectoweb.entity.Actividad;
import com.proyecto.proyectoweb.entity.Actividad.TipoActividad;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.service.ActividadService;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActividadServiceTest {

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ActividadService actividadService;

    private Actividad actividad;
    private ActividadDTO actividadDTO;
    private Long procesoId;
    private Long actividadId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        actividadId = 1L;
        
        actividad = new Actividad();
        actividad.setId(actividadId);
        actividad.setNombre("Test Actividad");
        actividad.setTipo(TipoActividad.TAREA);
        
        actividadDTO = new ActividadDTO();
        actividadDTO.setId(actividadId);
        actividadDTO.setNombre("Test Actividad");
        actividadDTO.setTipo("tarea");
    }

    @Test
    void listarPorProceso_Success() {
        List<Actividad> actividades = Arrays.asList(actividad);
        List<ActividadDTO> expectedDTOs = Arrays.asList(actividadDTO);
        Type listType = new TypeToken<List<ActividadDTO>>() {}.getType();

        when(actividadRepository.findByProcesoId(procesoId)).thenReturn(actividades);
        when(modelMapper.map(actividades, listType)).thenReturn(expectedDTOs);

        List<ActividadDTO> result = actividadService.listarPorProceso(procesoId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(actividadDTO.getNombre(), result.get(0).getNombre());
        verify(actividadRepository).findByProcesoId(procesoId);
    }

    @Test
    void listarPorProceso_EmptyList() {
        List<Actividad> actividades = Arrays.asList();
        List<ActividadDTO> expectedDTOs = Arrays.asList();
        Type listType = new TypeToken<List<ActividadDTO>>() {}.getType();

        when(actividadRepository.findByProcesoId(procesoId)).thenReturn(actividades);
        when(modelMapper.map(actividades, listType)).thenReturn(expectedDTOs);

        List<ActividadDTO> result = actividadService.listarPorProceso(procesoId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void obtenerActividad_Success() {
        when(actividadRepository.findById(actividadId)).thenReturn(Optional.of(actividad));
        when(modelMapper.map(actividad, ActividadDTO.class)).thenReturn(actividadDTO);

        ActividadDTO result = actividadService.obtenerActividad(actividadId);

        assertNotNull(result);
        assertEquals(actividadDTO.getNombre(), result.getNombre());
        verify(actividadRepository).findById(actividadId);
    }

    @Test
    void obtenerActividad_NotFound() {
        when(actividadRepository.findById(actividadId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            actividadService.obtenerActividad(actividadId));
    }

    @Test
    void crearActividad_Success() {
        when(modelMapper.map(actividadDTO, Actividad.class)).thenReturn(actividad);
        when(actividadRepository.save(actividad)).thenReturn(actividad);
        when(modelMapper.map(actividad, ActividadDTO.class)).thenReturn(actividadDTO);

        ActividadDTO result = actividadService.crearActividad(actividadDTO);

        assertNotNull(result);
        assertEquals(actividadDTO.getNombre(), result.getNombre());
        verify(actividadRepository).save(actividad);
    }

    @Test
    void actualizarActividad_Success() {
        ActividadDTO updateDTO = new ActividadDTO();
        updateDTO.setNombre("Updated Actividad");

        when(actividadRepository.findById(actividadId)).thenReturn(Optional.of(actividad));
        doAnswer(invocation -> {
            ActividadDTO source = invocation.getArgument(0);
            Actividad destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, actividad);
        when(actividadRepository.save(actividad)).thenReturn(actividad);
        when(modelMapper.map(actividad, ActividadDTO.class)).thenReturn(actividadDTO);

        ActividadDTO result = actividadService.actualizarActividad(actividadId, updateDTO);

        assertNotNull(result);
        verify(actividadRepository).findById(actividadId);
        verify(actividadRepository).save(actividad);
    }

    @Test
    void actualizarActividad_NotFound() {
        when(actividadRepository.findById(actividadId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            actividadService.actualizarActividad(actividadId, actividadDTO));
    }

    @Test
    void eliminarActividad_Success() {
        when(actividadRepository.existsById(actividadId)).thenReturn(true);
        doNothing().when(actividadRepository).deleteById(actividadId);

        assertDoesNotThrow(() -> actividadService.eliminarActividad(actividadId));
        verify(actividadRepository).deleteById(actividadId);
    }

    @Test
    void eliminarActividad_NotFound() {
        when(actividadRepository.existsById(actividadId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> 
            actividadService.eliminarActividad(actividadId));
        verify(actividadRepository, never()).deleteById(any());
    }
}