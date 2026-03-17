package com.proyecto.proyectoweb.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProcesoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String estado;
    private Long empresaId;
    private Long usuarioId;

    private List<ActividadDTO> actividades;
    private List<ArcoDTO> arcos;
    private List<GatewayDTO> gateways;
}