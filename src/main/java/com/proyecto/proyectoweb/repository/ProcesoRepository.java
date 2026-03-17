package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProcesoRepository extends JpaRepository<Proceso, Long> {

   
    List<Proceso> findByEmpresaId(Long empresaId);


    List<Proceso> findByEmpresaIdAndEstado(Long empresaId, Proceso.EstadoProceso estado);

   
    List<Proceso> findByEmpresaIdAndCategoria(Long empresaId, String categoria);

    
    @Query("SELECT p FROM Proceso p WHERE p.empresa.id = :empresaId " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:categoria IS NULL OR p.categoria LIKE %:categoria%)")
    List<Proceso> buscarConFiltros(@Param("empresaId") Long empresaId, 
                                   @Param("estado") Proceso.EstadoProceso estado, 
                                   @Param("categoria") String categoria);
}