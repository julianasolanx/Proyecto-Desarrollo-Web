package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ProcesoRepository extends JpaRepository<Proceso, Long> {

    @Query("SELECT p FROM Proceso p WHERE p.empresa.id = :empresaId")
    List<Proceso> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT p FROM Proceso p WHERE p.empresa.id = :empresaId AND (:estado IS NULL OR CAST(p.estado AS string) = :estado) AND (:categoria IS NULL OR p.categoria = :categoria)")
    List<Proceso> findByEmpresaIdAndFiltros(@Param("empresaId") Long empresaId,
                                            @Param("estado") String estado,
                                            @Param("categoria") String categoria);

    @Modifying
    @Transactional
    @Query("UPDATE Proceso p SET p.estado = :estado WHERE p.id = :id")
    int updateEstado(@Param("id") Long id, @Param("estado") String estado);
}