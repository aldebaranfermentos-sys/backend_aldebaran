package com.app.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(
        @NotBlank String nombre,
        String telefono,
        String email,
        String direccion
) {}
