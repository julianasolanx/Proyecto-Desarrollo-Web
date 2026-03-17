package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String correoContacto;
    private String status;
}