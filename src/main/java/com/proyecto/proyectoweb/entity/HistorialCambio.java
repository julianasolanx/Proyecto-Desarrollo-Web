package com.proyecto.proyectoweb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidad;       // "PROCESO" o "ACTIVIDAD"
    private Long entidadId;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDateTime fechaCambio;

    @ManyToOne
    @JoinColumn(name = "proceso_id")
    private Proceso proceso;
}
