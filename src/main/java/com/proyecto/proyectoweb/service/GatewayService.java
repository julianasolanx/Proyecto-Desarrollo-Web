package com.proyecto.proyectoweb.service;
import com.proyecto.proyectoweb.dto.GatewayDTO;
import com.proyecto.proyectoweb.entity.Gateway;
import com.proyecto.proyectoweb.repository.GatewayRepository;
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
    private final ModelMapper modelMapper;

    public GatewayService(GatewayRepository gatewayRepository, ModelMapper modelMapper) {
        this.gatewayRepository = gatewayRepository;
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
        Gateway gateway = modelMapper.map(dto, Gateway.class);
        Gateway saved = gatewayRepository.save(gateway);
        return modelMapper.map(saved, GatewayDTO.class);
    }

    @Transactional
    public GatewayDTO actualizarGateway(Long id, GatewayDTO dto) {
        Gateway existing = gatewayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gateway no encontrado"));
        modelMapper.map(dto, existing);
        Gateway saved = gatewayRepository.save(existing);
        return modelMapper.map(saved, GatewayDTO.class);
    }

    @Transactional
    public void eliminarGateway(Long id) {
        if (!gatewayRepository.existsById(id)) {
            throw new EntityNotFoundException("Gateway no encontrado");
        }
        gatewayRepository.deleteById(id);
    }
}