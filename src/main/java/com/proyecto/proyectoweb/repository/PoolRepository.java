package com.proyecto.proyectoweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.Pool;

public interface PoolRepository extends JpaRepository<Pool, Long> {
    Optional<Pool> findByEmpresaId(Long empresaId);
    boolean existsByEmpresaId(Long empresaId);
}
