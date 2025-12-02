package com.app.inventario.dto;

public record EquipoResponse(
        Long id,
        String nombre,
        String estado,
        String ubicacion,
        String observacion
) {}