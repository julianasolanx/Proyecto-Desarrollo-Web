package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    @Query("SELECT a FROM Actividad a WHERE a.proceso.id = :procesoId")
    List<Actividad> findByProcesoId(@Param("procesoId") Long procesoId);

    @Modifying
    @Transactional
    @Query("UPDATE Actividad a SET a.tipo = :tipo WHERE a.id = :id")
    int updateTipo(@Param("id") Long id, @Param("tipo") String tipo);
}