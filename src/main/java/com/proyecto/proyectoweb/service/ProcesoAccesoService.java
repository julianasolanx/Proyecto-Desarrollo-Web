package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.ProcesoAccesoDTO;
import com.proyecto.proyectoweb.entity.Empresa;
import com.proyecto.proyectoweb.entity.Proceso;
import com.proyecto.proyectoweb.entity.ProcesoAcceso;
import com.proyecto.proyectoweb.repository.ProcesoAccesoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcesoAccesoService {

    private final ProcesoAccesoRepository accesoRepository;
    private final ProcesoService procesoService;
    private final EmpresaService empresaService;
    private final HistorialCambioService historialCambioService;

    @Transactional(readOnly = true)
    public List<ProcesoAccesoDTO> listarPorProceso(Long procesoId) {
        return accesoRepository.findByProcesoId(procesoId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProcesoAccesoDTO> listarPorEmpresa(Long empresaId) {
        return accesoRepository.findByEmpresaId(empresaId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProcesoAccesoDTO otorgarAcceso(ProcesoAccesoDTO dto) {
        Proceso proceso = procesoService.obtenerEntidad(dto.getProcesoId());
        if (!proceso.getEsCompartido()) {
            throw new IllegalStateException("El proceso no está marcado como compartido.");
        }
        Empresa empresa = empresaService.obtenerEntidad(dto.getEmpresaId());
        if (proceso.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No se puede otorgar acceso a la empresa propietaria.");
        }
        if (accesoRepository.existsByProcesoIdAndEmpresaId(proceso.getId(), empresa.getId())) {
            throw new IllegalStateException("La empresa ya tiene acceso a este proceso.");
        }
        ProcesoAcceso acceso = new ProcesoAcceso();
        acceso.setProceso(proceso);
        acceso.setEmpresa(empresa);
        acceso.setTipoAcceso(ProcesoAcceso.TipoAcceso.valueOf(dto.getTipoAcceso()));
        ProcesoAcceso saved = accesoRepository.save(acceso);
        historialCambioService.registrarCambio("PROCESO_ACCESO", proceso.getId(), "acceso_otorgado",
                null, empresa.getNombre() + ":" + dto.getTipoAcceso(), null);
        return toDTO(saved);
    }

    @Transactional
    public void revocarAcceso(Long accesoId) {
        ProcesoAcceso acceso = accesoRepository.findById(accesoId)
                .orElseThrow(() -> new EntityNotFoundException("Acceso no encontrado: " + accesoId));
        historialCambioService.registrarCambio("PROCESO_ACCESO", acceso.getProceso().getId(),
                "acceso_revocado", acceso.getEmpresa().getNombre(), null, null);
        accesoRepository.deleteById(accesoId);
    }

    private ProcesoAccesoDTO toDTO(ProcesoAcceso a) {
        ProcesoAccesoDTO dto = new ProcesoAccesoDTO();
        dto.setId(a.getId());
        dto.setProcesoId(a.getProceso().getId());
        dto.setEmpresaId(a.getEmpresa().getId());
        dto.setEmpresaNombre(a.getEmpresa().getNombre());
        dto.setTipoAcceso(a.getTipoAcceso().name());
        return dto;
    }
}
