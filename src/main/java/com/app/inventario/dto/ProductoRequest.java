package com.app.inventario.dto;

import jakarta.validation.constraints.*;

public record ProductoRequest(
        @NotBlank String nombre,
        String descripcion,
        String unidadMedida,
        @Min(0) Integer stockMinimo,
        Integer categoriaId,
        Integer unidadMedidaId
) {}
