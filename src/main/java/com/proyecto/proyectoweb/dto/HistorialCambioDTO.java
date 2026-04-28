package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class HistorialCambioDTO {
    private Long id;
    private String entidad;
    private Long entidadId;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private String fechaCambio;
    private Long procesoId;
}
