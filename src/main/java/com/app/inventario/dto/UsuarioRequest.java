package com.app.inventario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 100) String apellido,
        @Email @Size(max = 150) String correo,
        @NotBlank @Size(max = 255) String contrasena,
        String rol
) {}
