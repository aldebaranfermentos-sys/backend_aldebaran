package com.app.inventario.dto;

import jakarta.validation.constraints.Email;

public record UsuarioUpdateRequest(
        String nombre,
        String apellido,
        @Email String correo,
        String contrasena, // opcional en update
        String rol
) {}
