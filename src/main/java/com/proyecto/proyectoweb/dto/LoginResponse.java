package com.proyecto.proyectoweb.dto;

public record LoginResponse(
    String token,
    String type,
    long expiresIn,
    String correo,
    String rol,
    Long id,
    String nombre
) {
    public LoginResponse(String token, long expiresIn) {
        this(token, "Bearer", expiresIn, null, null, null, null);
    }

    public LoginResponse(
        String token,
        long expiresIn,
        String correo,
        String rol,
        Long id,
        String nombre
    ) {
        this(token, "Bearer", expiresIn, correo, rol, id, nombre);
    }
}
