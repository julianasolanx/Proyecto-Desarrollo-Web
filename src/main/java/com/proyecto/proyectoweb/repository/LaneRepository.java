package com.proyecto.proyectoweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.proyectoweb.entity.Lane;

public interface LaneRepository extends JpaRepository<Lane, Long> {
    List<Lane> findByPoolId(Long poolId);
    List<Lane> findByPoolEmpresaId(Long empresaId);
}
