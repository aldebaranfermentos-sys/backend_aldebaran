package com.app.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(String nombre, String descripcion) {}

