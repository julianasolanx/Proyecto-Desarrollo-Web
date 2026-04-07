package com.proyecto.proyectoweb.serviceTest;

import com.proyecto.proyectoweb.dto.GatewayDTO;
import com.proyecto.proyectoweb.entity.Gateway;
import com.proyecto.proyectoweb.entity.Gateway.TipoGateway;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.GatewayRepository;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private ArcoService arcoService;

    @Mock
    private ProcesoService procesoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GatewayService gatewayService;

    private Gateway gateway;
    private GatewayDTO gatewayDTO;
    private Proceso proceso;
    private Long procesoId;
    private Long gatewayId;

    @BeforeEach
    void setUp() {
        procesoId = 1L;
        gatewayId = 1L;

        proceso = new Proceso();
        proceso.setId(procesoId);

        gateway = new Gateway();
        gateway.setId(gatewayId);
        gateway.setNombre("Test Gateway");
        gateway.setTipo(TipoGateway.EXCLUSIVO);

        gatewayDTO = new GatewayDTO();
        gatewayDTO.setId(gatewayId);
        gatewayDTO.setNombre("Test Gateway");
        gatewayDTO.setTipo("EXCLUSIVO");
        gatewayDTO.setProcesoId(procesoId);
    }

    @Test
    void listarGateways_Success() {
        List<Gateway> gateways = Arrays.asList(gateway);
        List<GatewayDTO> expectedDTOs = Arrays.asList(gatewayDTO);
        Type listType = new TypeToken<List<GatewayDTO>>() {}.getType();

        when(gatewayRepository.findAll()).thenReturn(gateways);
        when(modelMapper.map(gateways, listType)).thenReturn(expectedDTOs);

        List<GatewayDTO> result = gatewayService.listarGateways();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gatewayRepository).findAll();
    }

    @Test
    void listarPorProceso_Success() {
        List<Gateway> gateways = Arrays.asList(gateway);
        List<GatewayDTO> expectedDTOs = Arrays.asList(gatewayDTO);
        Type listType = new TypeToken<List<GatewayDTO>>() {}.getType();

        when(gatewayRepository.findByProcesoId(procesoId)).thenReturn(gateways);
        when(modelMapper.map(gateways, listType)).thenReturn(expectedDTOs);

        List<GatewayDTO> result = gatewayService.listarPorProceso(procesoId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(gatewayDTO.getNombre(), result.get(0).getNombre());
    }

    @Test
    void obtenerGateway_Success() {
        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(gateway));
        when(modelMapper.map(gateway, GatewayDTO.class)).thenReturn(gatewayDTO);

        GatewayDTO result = gatewayService.obtenerGateway(gatewayId);

        assertNotNull(result);
        assertEquals(gatewayDTO.getNombre(), result.getNombre());
    }

    @Test
    void obtenerGateway_NotFound() {
        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            gatewayService.obtenerGateway(gatewayId));
    }

    @Test
    void crearGateway_Success() {
        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        when(modelMapper.map(gatewayDTO, Gateway.class)).thenReturn(gateway);
        when(gatewayRepository.save(gateway)).thenReturn(gateway);
        when(modelMapper.map(gateway, GatewayDTO.class)).thenReturn(gatewayDTO);

        GatewayDTO result = gatewayService.crearGateway(gatewayDTO);

        assertNotNull(result);
        assertEquals(gatewayDTO.getNombre(), result.getNombre());
    }

    @Test
    void crearGateway_ProcesoNotFound() {
        when(procesoService.obtenerEntidad(procesoId)).thenThrow(new EntityNotFoundException("Proceso no encontrado"));

        assertThrows(EntityNotFoundException.class, () ->
            gatewayService.crearGateway(gatewayDTO));
    }

    @Test
    void actualizarGateway_Success() {
        GatewayDTO updateDTO = new GatewayDTO();
        updateDTO.setNombre("Updated Gateway");
        updateDTO.setProcesoId(procesoId);

        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(gateway));
        when(procesoService.obtenerEntidad(procesoId)).thenReturn(proceso);
        doAnswer(invocation -> {
            GatewayDTO source = invocation.getArgument(0);
            Gateway destination = invocation.getArgument(1);
            destination.setNombre(source.getNombre());
            return null;
        }).when(modelMapper).map(updateDTO, gateway);
        when(gatewayRepository.save(gateway)).thenReturn(gateway);
        when(modelMapper.map(gateway, GatewayDTO.class)).thenReturn(gatewayDTO);

        GatewayDTO result = gatewayService.actualizarGateway(gatewayId, updateDTO);

        assertNotNull(result);
        verify(gatewayRepository).findById(gatewayId);
        verify(gatewayRepository).save(gateway);
    }

    @Test
    void actualizarGateway_NullProcesoId() {
        GatewayDTO updateDTO = new GatewayDTO();
        updateDTO.setNombre("Updated Gateway");

        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(gateway));
        doAnswer(invocation -> null).when(modelMapper).map(updateDTO, gateway);
        when(gatewayRepository.save(gateway)).thenReturn(gateway);
        when(modelMapper.map(gateway, GatewayDTO.class)).thenReturn(gatewayDTO);

        GatewayDTO result = gatewayService.actualizarGateway(gatewayId, updateDTO);

        assertNotNull(result);
        verify(procesoService, never()).obtenerEntidad(any());
    }

    @Test
    void actualizarGateway_NotFound() {
        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            gatewayService.actualizarGateway(gatewayId, gatewayDTO));
    }

    @Test
    void eliminarGateway_Success() {
        when(gatewayRepository.existsById(gatewayId)).thenReturn(true);
        when(arcoService.existenArcosPorGateway(gatewayId)).thenReturn(false);
        doNothing().when(gatewayRepository).deleteById(gatewayId);

        assertDoesNotThrow(() -> gatewayService.eliminarGateway(gatewayId));
        verify(gatewayRepository).deleteById(gatewayId);
    }

    @Test
    void eliminarGateway_ConArcos_ThrowsConflict() {
        when(gatewayRepository.existsById(gatewayId)).thenReturn(true);
        when(arcoService.existenArcosPorGateway(gatewayId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
            gatewayService.eliminarGateway(gatewayId));
        verify(gatewayRepository, never()).deleteById(any());
    }

    @Test
    void eliminarGateway_NotFound() {
        when(gatewayRepository.existsById(gatewayId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            gatewayService.eliminarGateway(gatewayId));
    }
}
