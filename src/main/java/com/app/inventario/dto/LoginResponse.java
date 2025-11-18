// LoginResponse.java
package com.app.inventario.dto;

public record LoginResponse(
        Integer id,
        String nombre,
        String email,
        String rol,
        String token
) {}
