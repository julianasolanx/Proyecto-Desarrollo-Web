package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.RolProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface RolProcesoRepository extends JpaRepository<RolProceso, Long> {

    @Query("SELECT r FROM RolProceso r WHERE r.empresa.id = :empresaId")
    List<RolProceso> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Modifying
    @Transactional
    @Query("UPDATE RolProceso r SET r.nombre = :nombre WHERE r.id = :id")
    int updateNombre(@Param("id") Long id, @Param("nombre") String nombre);
}