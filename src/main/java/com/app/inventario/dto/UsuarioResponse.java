package com.app.inventario.dto;

public record UsuarioResponse(
        Integer id,
        String nombre,
        String apellido,
        String correo,
        String rol
) {}
