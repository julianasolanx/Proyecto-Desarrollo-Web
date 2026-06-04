package com.proyecto.proyectoweb.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreMensaje;
    private String businessKey;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;

    @ManyToOne
    @JoinColumn(name = "mensaje_id")
    private MensajeProceso mensaje;

    @ManyToOne
    @JoinColumn(name = "proceso_emisor_id")
    private Proceso procesoEmisor;

    @ManyToOne
    @JoinColumn(name = "proceso_receptor_id")
    private Proceso procesoReceptor;

    public enum EstadoEvento {
        PENDIENTE, ENTREGADO, ERROR, NO_CORRELACIONADO
    }
}
