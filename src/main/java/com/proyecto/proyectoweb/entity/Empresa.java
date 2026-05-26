package com.proyecto.proyectoweb.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@SQLDelete(sql = "UPDATE empresa SET status = 1 WHERE id=?")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String nit;
    private String correoContacto;
    private Integer status = 0;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Proceso> procesos;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<RolProceso> roles;
}
