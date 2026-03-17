package com.proyecto.proyectoweb.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVO'")
@SQLDelete(sql = "UPDATE proceso SET status = 'INACTIVO' WHERE id=?")
public class Proceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;
    private String categoria;

    private EstadoProceso estado;

    private String status = "ACTIVO";

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
    private List<Actividad> actividades;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
    private List<Gateway> gateways;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
    private List<Arco> arcos;

    public enum EstadoProceso {
        BORRADOR, PUBLICADO, INACTIVO
    }
}