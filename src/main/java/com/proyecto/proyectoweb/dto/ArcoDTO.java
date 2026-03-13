package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class ArcoDTO {
    private Long id;
    private String condicion;
    private Long procesoId;
    private Long actividadOrigenId;
    private Long gatewayOrigenId;
    private Long actividadDestinoId;
    private Long gatewayDestinoId;
}