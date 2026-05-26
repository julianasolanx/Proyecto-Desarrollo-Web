package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("SELECT e FROM Empresa e WHERE e.nit = :nit")
    Optional<Empresa> findByNit(@Param("nit") String nit);

    @Query("SELECT e FROM Empresa e WHERE e.correoContacto = :correo")
    Optional<Empresa> findByCorreoContacto(@Param("correo") String correo);

    @Query(value = "SELECT COUNT(*) > 0 FROM empresas WHERE nit = :nit", nativeQuery = true)
    boolean existsByNit(@Param("nit") String nit);

    @Modifying
    @Transactional
    @Query("UPDATE Empresa e SET e.nombre = :nombre WHERE e.id = :id")
    int updateNombre(@Param("id") Long id, @Param("nombre") String nombre);
}