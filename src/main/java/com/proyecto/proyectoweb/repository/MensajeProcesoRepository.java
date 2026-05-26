package com.proyecto.proyectoweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.MensajeProceso;

public interface MensajeProcesoRepository extends JpaRepository<MensajeProceso, Long> {
    List<MensajeProceso> findByProcesoId(Long procesoId);
    List<MensajeProceso> findByNombreAndTipo(String nombre, MensajeProceso.TipoMensaje tipo);
}
