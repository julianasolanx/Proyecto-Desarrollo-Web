package com.proyecto.proyectoweb.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@SQLRestriction("status = '0'")
@SQLDelete(sql = "UPDATE mensaje_proceso SET status = 1 WHERE id=?")
public class MensajeProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoMensaje tipo;

    @Column(columnDefinition = "TEXT")
    private String payloadTemplate;

    private String businessKey;

    private Integer status = 0;

    @ManyToOne
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL)
    private List<EventoMensaje> eventos;

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL)
    private List<ReglaCorrelacion> reglasCorrelacion;

    public enum TipoMensaje {
        THROW, CATCH
    }
}
