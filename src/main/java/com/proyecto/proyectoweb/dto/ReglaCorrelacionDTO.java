package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class ReglaCorrelacionDTO {
    private Long id;
    private String nombreMensaje;
    private String tipoCorrelacion;
    private String valorCorrelacion;
    private String politicaMultiple;
    private Long procesoId;
    private Long mensajeId;
}
