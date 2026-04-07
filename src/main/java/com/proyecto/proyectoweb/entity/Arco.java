package com.proyecto.proyectoweb.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("status = '0'")
@SQLDelete(sql = "UPDATE arco SET status = 1 WHERE id=?")
public class Arco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String condicion;

    private Integer status = 0;

    @ManyToOne
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne
    @JoinColumn(name = "actividad_origen_id")
    private Actividad actividadOrigen;

    @ManyToOne
    @JoinColumn(name = "gateway_origen_id")
    private Gateway gatewayOrigen;

    @ManyToOne
    @JoinColumn(name = "actividad_destino_id")
    private Actividad actividadDestino;

    @ManyToOne
    @JoinColumn(name = "gateway_destino_id")
    private Gateway gatewayDestino;
}
