package com.proyecto.proyectoweb.repository;

import com.proyecto.proyectoweb.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo")
    Optional<Usuario> findByCorreo(@Param("correo") String correo);

    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId")
    List<Usuario> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Query(value = "SELECT COUNT(*) > 0 FROM usuarios WHERE correo = :correo", nativeQuery = true)
    boolean existsByCorreo(@Param("correo") String correo);

    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.contrasena = :contrasena")
    Optional<Usuario> login(@Param("correo") String correo, @Param("contrasena") String contrasena);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.rol = :rol WHERE u.id = :id")
    int updateRol(@Param("id") Long id, @Param("rol") String rol);
}