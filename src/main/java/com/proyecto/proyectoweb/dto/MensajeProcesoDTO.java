package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class MensajeProcesoDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String payloadTemplate;
    private String businessKey;
    private Long procesoId;
    private Long actividadId;
}
