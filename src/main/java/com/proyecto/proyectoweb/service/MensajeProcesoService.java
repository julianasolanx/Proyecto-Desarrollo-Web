package com.proyecto.proyectoweb.service;

import com.proyecto.proyectoweb.dto.EventoMensajeDTO;
import com.proyecto.proyectoweb.dto.MensajeProcesoDTO;
import com.proyecto.proyectoweb.dto.ReglaCorrelacionDTO;
import com.proyecto.proyectoweb.entity.*;
import com.proyecto.proyectoweb.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeProcesoService {

    private final MensajeProcesoRepository mensajeRepository;
    private final EventoMensajeRepository eventoRepository;
    private final ReglaCorrelacionRepository reglaRepository;
    private final ProcesoService procesoService;

    // ─── MENSAJES ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MensajeProcesoDTO> listarPorProceso(Long procesoId) {
        return mensajeRepository.findByProcesoId(procesoId).stream()
                .map(this::toMensajeDTO).collect(Collectors.toList());
    }

    @Transactional
    public MensajeProcesoDTO crear(MensajeProcesoDTO dto) {
        Proceso proceso = procesoService.obtenerEntidad(dto.getProcesoId());
        MensajeProceso m = new MensajeProceso();
        m.setNombre(dto.getNombre());
        m.setTipo(MensajeProceso.TipoMensaje.valueOf(dto.getTipo()));
        m.setPayloadTemplate(dto.getPayloadTemplate());
        m.setBusinessKey(dto.getBusinessKey());
        m.setProceso(proceso);
        return toMensajeDTO(mensajeRepository.save(m));
    }

    @Transactional
    public MensajeProcesoDTO actualizar(Long id, MensajeProcesoDTO dto) {
        MensajeProceso m = findMensajeById(id);
        m.setNombre(dto.getNombre());
        m.setTipo(MensajeProceso.TipoMensaje.valueOf(dto.getTipo()));
        m.setPayloadTemplate(dto.getPayloadTemplate());
        m.setBusinessKey(dto.getBusinessKey());
        return toMensajeDTO(mensajeRepository.save(m));
    }

    @Transactional
    public void eliminarMensaje(Long id) {
        if (!mensajeRepository.existsById(id)) throw new EntityNotFoundException("Mensaje no encontrado: " + id);
        mensajeRepository.deleteById(id);
    }

    // ─── THROW: lanzar mensaje y correlacionar con CATCH (HU-25 + HU-28) ───

    @Transactional
    public EventoMensajeDTO lanzarMensaje(Long mensajeId, String payload, String businessKey) {
        MensajeProceso throw_ = findMensajeById(mensajeId);
        if (throw_.getTipo() != MensajeProceso.TipoMensaje.THROW) {
            throw new IllegalArgumentException("El mensaje no es de tipo THROW.");
        }

        EventoMensaje evento = new EventoMensaje();
        evento.setNombreMensaje(throw_.getNombre());
        evento.setBusinessKey(businessKey != null ? businessKey : throw_.getBusinessKey());
        evento.setPayload(payload);
        evento.setMensaje(throw_);
        evento.setProcesoEmisor(throw_.getProceso());
        evento.setFechaEnvio(LocalDateTime.now());

        // Buscar CATCH con el mismo nombre (correlación HU-28)
        List<MensajeProceso> catches = mensajeRepository.findByNombreAndTipo(
                throw_.getNombre(), MensajeProceso.TipoMensaje.CATCH);

        if (catches.isEmpty()) {
            evento.setEstado(EventoMensaje.EstadoEvento.NO_CORRELACIONADO);
        } else {
            // Aplicar política: primera coincidencia por defecto
            MensajeProceso catch_ = aplicarPoliticaCorrelacion(catches, evento.getBusinessKey());
            if (catch_ != null) {
                evento.setProcesoReceptor(catch_.getProceso());
                evento.setEstado(EventoMensaje.EstadoEvento.ENTREGADO);
                evento.setFechaEntrega(LocalDateTime.now());
            } else {
                evento.setEstado(EventoMensaje.EstadoEvento.NO_CORRELACIONADO);
            }
        }

        return toEventoDTO(eventoRepository.save(evento));
    }

    private MensajeProceso aplicarPoliticaCorrelacion(List<MensajeProceso> catches, String businessKey) {
        // Buscar regla de correlación por businessKey
        for (MensajeProceso catch_ : catches) {
            List<ReglaCorrelacion> reglas = reglaRepository.findByNombreMensaje(catch_.getNombre());
            for (ReglaCorrelacion regla : reglas) {
                if (regla.getTipoCorrelacion() == ReglaCorrelacion.TipoCorrelacion.BUSINESS_KEY
                        && businessKey != null && businessKey.equals(regla.getValorCorrelacion())) {
                    return catch_;
                }
            }
        }
        // Sin regla explícita: retornar el primero (política PRIMERA)
        return catches.get(0);
    }

    // ─── EVENTOS (auditoría HU-25 + HU-28) ──────────────────────────────────

    @Transactional(readOnly = true)
    public List<EventoMensajeDTO> listarEventosPorProceso(Long procesoId) {
        List<EventoMensaje> emitidos = eventoRepository.findByProcesoEmisorId(procesoId);
        List<EventoMensaje> recibidos = eventoRepository.findByProcesoReceptorId(procesoId);
        emitidos.addAll(recibidos);
        return emitidos.stream().distinct().map(this::toEventoDTO).collect(Collectors.toList());
    }

    // ─── REGLAS DE CORRELACIÓN (HU-28) ──────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReglaCorrelacionDTO> listarReglasPorProceso(Long procesoId) {
        return reglaRepository.findByProcesoId(procesoId).stream()
                .map(this::toReglaDTO).collect(Collectors.toList());
    }

    @Transactional
    public ReglaCorrelacionDTO crearRegla(ReglaCorrelacionDTO dto) {
        Proceso proceso = procesoService.obtenerEntidad(dto.getProcesoId());
        ReglaCorrelacion r = new ReglaCorrelacion();
        r.setNombreMensaje(dto.getNombreMensaje());
        r.setTipoCorrelacion(ReglaCorrelacion.TipoCorrelacion.valueOf(dto.getTipoCorrelacion()));
        r.setValorCorrelacion(dto.getValorCorrelacion());
        r.setPoliticaMultiple(ReglaCorrelacion.PoliticaMultiple.valueOf(dto.getPoliticaMultiple()));
        r.setProceso(proceso);
        if (dto.getMensajeId() != null) r.setMensaje(findMensajeById(dto.getMensajeId()));
        return toReglaDTO(reglaRepository.save(r));
    }

    @Transactional
    public void eliminarRegla(Long id) {
        if (!reglaRepository.existsById(id)) throw new EntityNotFoundException("Regla no encontrada: " + id);
        reglaRepository.deleteById(id);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private MensajeProceso findMensajeById(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado: " + id));
    }

    private MensajeProcesoDTO toMensajeDTO(MensajeProceso m) {
        MensajeProcesoDTO dto = new MensajeProcesoDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setTipo(m.getTipo().name());
        dto.setPayloadTemplate(m.getPayloadTemplate());
        dto.setBusinessKey(m.getBusinessKey());
        dto.setProcesoId(m.getProceso().getId());
        if (m.getActividad() != null) dto.setActividadId(m.getActividad().getId());
        return dto;
    }

    private EventoMensajeDTO toEventoDTO(EventoMensaje e) {
        EventoMensajeDTO dto = new EventoMensajeDTO();
        dto.setId(e.getId());
        dto.setNombreMensaje(e.getNombreMensaje());
        dto.setBusinessKey(e.getBusinessKey());
        dto.setPayload(e.getPayload());
        dto.setEstado(e.getEstado().name());
        dto.setFechaEnvio(e.getFechaEnvio());
        dto.setFechaEntrega(e.getFechaEntrega());
        if (e.getMensaje() != null) dto.setMensajeId(e.getMensaje().getId());
        if (e.getProcesoEmisor() != null) dto.setProcesoEmisorId(e.getProcesoEmisor().getId());
        if (e.getProcesoReceptor() != null) dto.setProcesoReceptorId(e.getProcesoReceptor().getId());
        return dto;
    }

    private ReglaCorrelacionDTO toReglaDTO(ReglaCorrelacion r) {
        ReglaCorrelacionDTO dto = new ReglaCorrelacionDTO();
        dto.setId(r.getId());
        dto.setNombreMensaje(r.getNombreMensaje());
        dto.setTipoCorrelacion(r.getTipoCorrelacion().name());
        dto.setValorCorrelacion(r.getValorCorrelacion());
        dto.setPoliticaMultiple(r.getPoliticaMultiple().name());
        dto.setProcesoId(r.getProceso().getId());
        if (r.getMensaje() != null) dto.setMensajeId(r.getMensaje().getId());
        return dto;
    }
}
