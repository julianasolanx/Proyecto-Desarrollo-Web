package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class GatewayDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private Long procesoId;
}