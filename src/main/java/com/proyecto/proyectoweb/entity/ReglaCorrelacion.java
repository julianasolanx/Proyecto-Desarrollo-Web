package com.proyecto.proyectoweb.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@SQLRestriction("status = '0'")
@SQLDelete(sql = "UPDATE regla_correlacion SET status = 1 WHERE id=?")
public class ReglaCorrelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreMensaje;

    @Enumerated(EnumType.STRING)
    private TipoCorrelacion tipoCorrelacion;

    private String valorCorrelacion;

    @Enumerated(EnumType.STRING)
    private PoliticaMultiple politicaMultiple;

    private Integer status = 0;

    @ManyToOne
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne
    @JoinColumn(name = "mensaje_id")
    private MensajeProceso mensaje;

    public enum TipoCorrelacion {
        BUSINESS_KEY, VARIABLE, EXPRESION
    }

    public enum PoliticaMultiple {
        ERROR, PRIMERA, NUEVA_INSTANCIA
    }
}
