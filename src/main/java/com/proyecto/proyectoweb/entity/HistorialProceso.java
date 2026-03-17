package com.proyecto.proyectoweb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class HistorialProceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proceso_id")
    private Proceso proceso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String descripcionCambio;
    private LocalDateTime fechaCambio = LocalDateTime.now();
}