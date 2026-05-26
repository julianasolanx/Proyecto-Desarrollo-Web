package com.proyecto.proyectoweb.dto;

import lombok.Data;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String message;
}
