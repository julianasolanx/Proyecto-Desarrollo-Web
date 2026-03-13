package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Arco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ArcoRepository extends JpaRepository<Arco, Long> {

    @Query("SELECT a FROM Arco a WHERE a.proceso.id = :procesoId")
    List<Arco> findByProcesoId(@Param("procesoId") Long procesoId);

    @Modifying
    @Transactional
    @Query("UPDATE Arco a SET a.condicion = :condicion WHERE a.id = :id")
    int updateCondicion(@Param("id") Long id, @Param("condicion") String condicion);
}