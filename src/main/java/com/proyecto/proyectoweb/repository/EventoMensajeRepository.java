package com.proyecto.proyectoweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.EventoMensaje;

public interface EventoMensajeRepository extends JpaRepository<EventoMensaje, Long> {
    List<EventoMensaje> findByMensajeId(Long mensajeId);
    List<EventoMensaje> findByNombreMensajeAndBusinessKey(String nombreMensaje, String businessKey);
    List<EventoMensaje> findByProcesoEmisorId(Long procesoId);
    List<EventoMensaje> findByProcesoReceptorId(Long procesoId);
}
