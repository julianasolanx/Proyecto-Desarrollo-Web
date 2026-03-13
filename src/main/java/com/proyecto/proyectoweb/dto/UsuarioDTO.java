package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private Long empresaId;
}