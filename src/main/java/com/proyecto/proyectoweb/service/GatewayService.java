package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.GatewayDTO;
import com.proyecto.proyectoweb.entity.Gateway;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.repository.ArcoRepository;
import com.proyecto.proyectoweb.repository.GatewayRepository;
import com.proyecto.proyectoweb.repository.ProcesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class GatewayService {

    private final GatewayRepository gatewayRepository;
    private final ArcoRepository arcoRepository;
    private final ProcesoRepository procesoRepository;
    private final ModelMapper modelMapper;

    public GatewayService(GatewayRepository gatewayRepository,
                          ArcoRepository arcoRepository,
                          ProcesoRepository procesoRepository,
                          ModelMapper modelMapper) {
        this.gatewayRepository = gatewayRepository;
        this.arcoRepository = arcoRepository;
        this.procesoRepository = procesoRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<GatewayDTO> listarGateways() {
        List<Gateway> gateways = gatewayRepository.findAll();
        Type listType = new TypeToken<List<GatewayDTO>>() {}.getType();
        return modelMapper.map(gateways, listType);
    }

    @Transactional(readOnly = true)
    public List<GatewayDTO> listarPorProceso(Long procesoId) {
        List<Gateway> gateways = gatewayRepository.findByProcesoId(procesoId);
        Type listType = new TypeToken<List<GatewayDTO>>() {}.getType();
        return modelMapper.map(gateways, listType);
    }

    @Transactional(readOnly = true)
    public GatewayDTO obtenerGateway(Long id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway no encontrado"));
        return modelMapper.map(gateway, GatewayDTO.class);
    }

    @Transactional
    public GatewayDTO crearGateway(GatewayDTO dto) {
        Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));

        Gateway gateway = modelMapper.map(dto, Gateway.class);
        gateway.setProceso(proceso);

        return modelMapper.map(gatewayRepository.save(gateway), GatewayDTO.class);
    }

    @Transactional
    public GatewayDTO actualizarGateway(Long id, GatewayDTO dto) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway no encontrado"));

        dto.setId(id);
        modelMapper.map(dto, gateway);

        if (dto.getProcesoId() != null) {
            Proceso proceso = procesoRepository.findById(dto.getProcesoId())
                    .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
            gateway.setProceso(proceso);
        }

        return modelMapper.map(gatewayRepository.save(gateway), GatewayDTO.class);
    }

    @Transactional
    public void eliminarGateway(Long id) {
        if (!gatewayRepository.existsById(id)) {
            throw new EntityNotFoundException("Gateway no encontrado");
        }
        boolean tieneArcos = !arcoRepository.findByGatewayOrigenId(id).isEmpty()
                || !arcoRepository.findByGatewayDestinoId(id).isEmpty();
        if (tieneArcos) {
            throw new IllegalStateException("No se puede eliminar el gateway porque tiene arcos conectados. Elimine primero los arcos asociados.");
        }
        gatewayRepository.deleteById(id);
    }
}
