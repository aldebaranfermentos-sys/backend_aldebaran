package com.app.inventario.dto;

public record ProveedorResponse(
        Long id,
        String nombre,
        String telefono,
        String email,
        String direccion
) {}
