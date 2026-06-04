package com.proyecto.proyectoweb.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventoMensajeDTO {
    private Long id;
    private String nombreMensaje;
    private String businessKey;
    private String payload;
    private String estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;
    private Long mensajeId;
    private Long procesoEmisorId;
    private Long procesoReceptorId;
}
