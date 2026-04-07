package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class CrearUsuarioDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private Long empresaId;
}
