package com.proyecto.proyectoweb.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@SQLDelete(sql = "UPDATE rol_proceso SET status = 1 WHERE id=?")
public class RolProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Integer status = 0;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permiso", joinColumns = @JoinColumn(name = "rol_proceso_id"))
    @Enumerated(EnumType.STRING)
    private Set<Permiso> permisos = new HashSet<>();

    @OneToMany(mappedBy = "rolResponsable")
    private List<Actividad> actividades;

    @OneToMany(mappedBy = "rolProceso")
    private List<Usuario> usuarios;

    public enum Permiso {
        CREAR_PROCESO,
        EDITAR_PROCESO,
        ELIMINAR_PROCESO,
        PUBLICAR_PROCESO,
        GESTIONAR_ROLES,
        GESTIONAR_USUARIOS,
        VER_PROCESOS_COMPARTIDOS
    }
}
