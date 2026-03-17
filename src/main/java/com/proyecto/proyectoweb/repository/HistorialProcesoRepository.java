package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.HistorialProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialProcesoRepository extends JpaRepository<HistorialProceso, Long> {
}