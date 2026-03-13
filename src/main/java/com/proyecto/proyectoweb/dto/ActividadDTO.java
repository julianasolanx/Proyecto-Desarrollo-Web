package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class ActividadDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private Long procesoId;
    private Long rolResponsableId;
}