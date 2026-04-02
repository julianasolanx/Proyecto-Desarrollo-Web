package com.proyecto.proyectoweb.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@Where(clause = "status = 0")
@SQLDelete(sql = "UPDATE actividad SET status = 1 WHERE id=?")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private TipoActividad tipo;
    private Integer status = 0;

    @ManyToOne
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne
    @JoinColumn(name = "rol_responsable_id")
    private RolProceso rolResponsable;

    public enum TipoActividad {
        TAREA, SUBPROCESO, EVENTO_INICIO, EVENTO_FIN
    }
}