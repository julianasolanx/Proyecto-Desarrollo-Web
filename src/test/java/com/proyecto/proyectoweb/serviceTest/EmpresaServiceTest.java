package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.EmpresaDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.service.EmpresaService;

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
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;
    private EmpresaDTO empresaDTO;
    private Long empresaId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        
        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Test Empresa");
        empresa.setNit("123456789");
        
        empresaDTO = new EmpresaDTO();
        empresaDTO.setId(empresaId);
        empresaDTO.setNombre("Test Empresa");
        empresaDTO.setNit("123456789");
    }

    @Test
    void listarEmpresas_Success() {
        List<Empresa> empresas = Arrays.asList(empresa);
        List<EmpresaDTO> expectedDTOs = Arrays.asList(empresaDTO);
        Type listType = new TypeToken<List<EmpresaDTO>>() {}.getType();

        when(empresaRepository.findAll()).thenReturn(empresas);
        when(modelMapper.map(empresas, listType)).thenReturn(expectedDTOs);

        List<EmpresaDTO> result = empresaService.listarEmpresas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(empresaDTO.getNombre(), result.get(0).getNombre());
    }

    @Test
    void listarEmpresas_EmptyList() {
        List<Empresa> empresas = Arrays.asList();
        List<EmpresaDTO> expectedDTOs = Arrays.asList();
        Type listType = new TypeToken<List<EmpresaDTO>>() {}.getType();

        when(empresaRepository.findAll()).thenReturn(empresas);
        when(modelMapper.map(empresas, listType)).thenReturn(expectedDTOs);

        List<EmpresaDTO> result = empresaService.listarEmpresas();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void obtenerEmpresa_Success() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(modelMapper.map(empresa, EmpresaDTO.class)).thenReturn(empresaDTO);

        EmpresaDTO result = empresaService.obtenerEmpresa(empresaId);

        assertNotNull(result);
        assertEquals(empresaDTO.getNombre(), result.getNombre());
    }

    @Test
    void obtenerEmpresa_NotFound() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            empresaService.obtenerEmpresa(empresaId));
    }

    @Test
    void crearEmpresa_Success() {
        when(modelMapper.map(empresaDTO, Empresa.class)).thenReturn(empresa);
        when(empresaRepository.save(empresa)).thenReturn(empresa);
        when(modelMapper.map(empresa, EmpresaDTO.class)).thenReturn(empresaDTO);

        EmpresaDTO result = empresaService.crearEmpresa(empresaDTO);

        assertNotNull(result);
        assertEquals(empresaDTO.getNombre(), result.getNombre());
        verify(empresaRepository).save(empresa);
    }

    @Test
    void actualizarEmpresa_Success() {
        EmpresaDTO updateDTO = new EmpresaDTO();
        updateDTO.setNombre("Updated Empresa");

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        doAnswer(invocation -> {
            EmpresaDTO source = invocation.getArgument(0);
            Empresa destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, empresa);
        when(empresaRepository.save(empresa)).thenReturn(empresa);
        when(modelMapper.map(empresa, EmpresaDTO.class)).thenReturn(empresaDTO);

        EmpresaDTO result = empresaService.actualizarEmpresa(empresaId, updateDTO);

        assertNotNull(result);
        verify(empresaRepository).findById(empresaId);
        verify(empresaRepository).save(empresa);
    }

    @Test
    void actualizarEmpresa_NotFound() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            empresaService.actualizarEmpresa(empresaId, empresaDTO));
    }

    @Test
    void eliminarEmpresa_Success() {
        when(empresaRepository.existsById(empresaId)).thenReturn(true);
        doNothing().when(empresaRepository).deleteById(empresaId);

        assertDoesNotThrow(() -> empresaService.eliminarEmpresa(empresaId));
        verify(empresaRepository).deleteById(empresaId);
    }

    @Test
    void eliminarEmpresa_NotFound() {
        when(empresaRepository.existsById(empresaId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> 
            empresaService.eliminarEmpresa(empresaId));
        verify(empresaRepository, never()).deleteById(any());
    }
}