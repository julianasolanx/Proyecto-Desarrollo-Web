package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Long> {

    List<HistorialCambio> findByProcesoIdOrderByFechaCambioDesc(Long procesoId);

    List<HistorialCambio> findByEntidadAndEntidadIdOrderByFechaCambioDesc(String entidad, Long entidadId);
}
