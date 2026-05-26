package com.proyecto.proyectoweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.ProcesoAcceso;

public interface ProcesoAccesoRepository extends JpaRepository<ProcesoAcceso, Long> {
    List<ProcesoAcceso> findByProcesoId(Long procesoId);
    List<ProcesoAcceso> findByEmpresaId(Long empresaId);
    boolean existsByProcesoIdAndEmpresaId(Long procesoId, Long empresaId);
}
