package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.ArcoDTO;
import com.proyecto.proyectoweb.entity.Arco;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.service.ArcoService;

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
class ArcoServiceTest {

    @Mock
    private ArcoRepository arcoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ArcoService arcoService;

    private Arco arco;
    private ArcoDTO arcoDTO;
    private Long procesoId;
    private Long arcoId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        arcoId = 1L;
        
        arco = new Arco();
        arco.setId(arcoId);
        arco.setCondicion("condicion");
        
        arcoDTO = new ArcoDTO();
        arcoDTO.setId(arcoId);
        arcoDTO.setCondicion("condicion");
    }

    @Test
    void listarPorProceso_Success() {
        List<Arco> arcos = Arrays.asList(arco);
        List<ArcoDTO> expectedDTOs = Arrays.asList(arcoDTO);
        Type listType = new TypeToken<List<ArcoDTO>>() {}.getType();

        when(arcoRepository.findByProcesoId(procesoId)).thenReturn(arcos);
        when(modelMapper.map(arcos, listType)).thenReturn(expectedDTOs);

        List<ArcoDTO> result = arcoService.listarPorProceso(procesoId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(arcoDTO.getCondicion(), result.get(0).getCondicion());
    }

    @Test
    void obtenerArco_Success() {
        when(arcoRepository.findById(arcoId)).thenReturn(Optional.of(arco));
        when(modelMapper.map(arco, ArcoDTO.class)).thenReturn(arcoDTO);

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
        when(modelMapper.map(arcoDTO, Arco.class)).thenReturn(arco);
        when(arcoRepository.save(arco)).thenReturn(arco);
        when(modelMapper.map(arco, ArcoDTO.class)).thenReturn(arcoDTO);

        ArcoDTO result = arcoService.crearArco(arcoDTO);

        assertNotNull(result);
        assertEquals(arcoDTO.getCondicion(), result.getCondicion());
    }

    @Test
    void actualizarArco_Success() {
        ArcoDTO updateDTO = new ArcoDTO();
        updateDTO.setCondicion("Updated Arco");

        when(arcoRepository.findById(arcoId)).thenReturn(Optional.of(arco));
        doAnswer(invocation -> {
            ArcoDTO source = invocation.getArgument(0);
            Arco destination = invocation.getArgument(1);
            destination.setCondicion(source.getCondicion());
            return null;
        }).when(modelMapper).map(updateDTO, arco);
        when(arcoRepository.save(arco)).thenReturn(arco);
        when(modelMapper.map(arco, ArcoDTO.class)).thenReturn(arcoDTO);

        ArcoDTO result = arcoService.actualizarArco(arcoId, updateDTO);

        assertNotNull(result);
        verify(arcoRepository).findById(arcoId);
        verify(arcoRepository).save(arco);
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