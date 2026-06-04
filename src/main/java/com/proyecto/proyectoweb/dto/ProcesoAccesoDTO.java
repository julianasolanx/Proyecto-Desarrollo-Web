package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class ProcesoAccesoDTO {
    private Long id;
    private Long procesoId;
    private Long empresaId;
    private String empresaNombre;
    private String tipoAcceso;
}
