package com.app.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public record UnidadMedidaRequest(@NotBlank String nombre) {}
