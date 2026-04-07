package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.RolProcesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.RolProceso;
import com.proyecto.proyectoweb.repository.ActividadRepository;
import com.proyecto.proyectoweb.repository.EmpresaRepository;
import com.proyecto.proyectoweb.repository.RolProcesoRepository;
import com.proyecto.proyectoweb.service.RolProcesoService;

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
class RolProcesoServiceTest {

    @Mock
    private RolProcesoRepository rolProcesoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RolProcesoService rolProcesoService;

    private RolProceso rolProceso;
    private RolProcesoDTO rolProcesoDTO;
    private Empresa empresa;
    private Long empresaId;
    private Long rolId;

    @BeforeEach
    void setUp() {
        empresaId = 1L;
        rolId = 1L;

        empresa = new Empresa();
        empresa.setId(empresaId);

        rolProceso = new RolProceso();
        rolProceso.setId(rolId);
        rolProceso.setNombre("Test Rol");
        rolProceso.setDescripcion("Descripción del rol");

        rolProcesoDTO = new RolProcesoDTO();
        rolProcesoDTO.setId(rolId);
        rolProcesoDTO.setNombre("Test Rol");
        rolProcesoDTO.setDescripcion("Descripción del rol");
        rolProcesoDTO.setEmpresaId(empresaId);
    }

    @Test
    void listarPorEmpresa_Success() {
        List<RolProceso> roles = Arrays.asList(rolProceso);
        List<RolProcesoDTO> expectedDTOs = Arrays.asList(rolProcesoDTO);
        Type listType = new TypeToken<List<RolProcesoDTO>>() {}.getType();

        when(rolProcesoRepository.findByEmpresaId(empresaId)).thenReturn(roles);
        when(modelMapper.map(roles, listType)).thenReturn(expectedDTOs);

        List<RolProcesoDTO> result = rolProcesoService.listarPorEmpresa(empresaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rolProcesoDTO.getNombre(), result.get(0).getNombre());
    }

    @Test
    void obtenerRol_Success() {
        when(rolProcesoRepository.findById(rolId)).thenReturn(Optional.of(rolProceso));
        when(modelMapper.map(rolProceso, RolProcesoDTO.class)).thenReturn(rolProcesoDTO);

        RolProcesoDTO result = rolProcesoService.obtenerRol(rolId);

        assertNotNull(result);
        assertEquals(rolProcesoDTO.getNombre(), result.getNombre());
    }

    @Test
    void obtenerRol_NotFound() {
        when(rolProcesoRepository.findById(rolId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            rolProcesoService.obtenerRol(rolId));
    }

    @Test
    void crearRol_Success() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(modelMapper.map(rolProcesoDTO, RolProceso.class)).thenReturn(rolProceso);
        when(rolProcesoRepository.save(rolProceso)).thenReturn(rolProceso);
        when(modelMapper.map(rolProceso, RolProcesoDTO.class)).thenReturn(rolProcesoDTO);

        RolProcesoDTO result = rolProcesoService.crearRol(rolProcesoDTO);

        assertNotNull(result);
        assertEquals(rolProcesoDTO.getNombre(), result.getNombre());
    }

    @Test
    void crearRol_EmpresaNotFound() {
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            rolProcesoService.crearRol(rolProcesoDTO));
    }

    @Test
    void actualizarRol_Success() {
        RolProcesoDTO updateDTO = new RolProcesoDTO();
        updateDTO.setNombre("Updated Rol");
        updateDTO.setEmpresaId(empresaId);

        when(rolProcesoRepository.findById(rolId)).thenReturn(Optional.of(rolProceso));
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        doAnswer(invocation -> {
            RolProcesoDTO source = invocation.getArgument(0);
            RolProceso destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, rolProceso);
        when(rolProcesoRepository.save(rolProceso)).thenReturn(rolProceso);
        when(modelMapper.map(rolProceso, RolProcesoDTO.class)).thenReturn(rolProcesoDTO);

        RolProcesoDTO result = rolProcesoService.actualizarRol(rolId, updateDTO);

        assertNotNull(result);
        verify(rolProcesoRepository).findById(rolId);
        verify(rolProcesoRepository).save(rolProceso);
    }

    @Test
    void eliminarRol_Success() {
        when(rolProcesoRepository.existsById(rolId)).thenReturn(true);
        when(actividadRepository.existsByRolResponsableId(rolId)).thenReturn(false);
        doNothing().when(rolProcesoRepository).deleteById(rolId);

        assertDoesNotThrow(() -> rolProcesoService.eliminarRol(rolId));
        verify(rolProcesoRepository).deleteById(rolId);
    }

    @Test
    void eliminarRol_EnUso() {
        when(rolProcesoRepository.existsById(rolId)).thenReturn(true);
        when(actividadRepository.existsByRolResponsableId(rolId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
            rolProcesoService.eliminarRol(rolId));
        verify(rolProcesoRepository, never()).deleteById(any());
    }

    @Test
    void eliminarRol_NotFound() {
        when(rolProcesoRepository.existsById(rolId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            rolProcesoService.eliminarRol(rolId));
    }
}
