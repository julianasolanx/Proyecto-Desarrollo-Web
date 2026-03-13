package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    @Query("SELECT g FROM Gateway g WHERE g.proceso.id = :procesoId")
    List<Gateway> findByProcesoId(@Param("procesoId") Long procesoId);

    @Modifying
    @Transactional
    @Query("UPDATE Gateway g SET g.tipo = :tipo WHERE g.id = :id")
    int updateTipo(@Param("id") Long id, @Param("tipo") String tipo);
}