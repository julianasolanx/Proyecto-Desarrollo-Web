package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class PoolDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long empresaId;
    private String empresaNombre;
}
