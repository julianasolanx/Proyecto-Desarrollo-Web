package com.proyecto.proyectoweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.ReglaCorrelacion;

public interface ReglaCorrelacionRepository extends JpaRepository<ReglaCorrelacion, Long> {
    List<ReglaCorrelacion> findByProcesoId(Long procesoId);
    List<ReglaCorrelacion> findByNombreMensaje(String nombreMensaje);
}
