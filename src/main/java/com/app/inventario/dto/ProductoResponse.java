package com.app.inventario.dto;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        String unidadMedida,
        Integer stockMinimo,
        Integer categoriaId,
        Integer unidadMedidaId
) {}
