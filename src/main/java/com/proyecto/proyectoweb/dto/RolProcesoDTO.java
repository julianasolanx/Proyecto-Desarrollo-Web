package com.proyecto.proyectoweb.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RolProcesoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long empresaId;
    private List<Long> actividadIds;
    private Set<String> permisos;
}