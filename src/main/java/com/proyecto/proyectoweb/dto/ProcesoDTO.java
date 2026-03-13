package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class ProcesoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String estado;
    private Long empresaId;
}