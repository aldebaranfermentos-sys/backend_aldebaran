package com.app.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipoRequest(
        @NotBlank String nombre,
        @NotBlank String estado,
        String ubicacion,
        String observacion
) {}