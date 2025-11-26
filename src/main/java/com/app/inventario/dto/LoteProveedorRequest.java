package com.app.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoteProveedorRequest(
        @NotBlank String numeroLote,
        LocalDate fechaIngreso,
        LocalDate fechaVencimiento,
        @NotNull Long proveedorId,
        @NotNull Long productoId
) {}
