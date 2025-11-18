package com.app.inventario.controller;

import com.app.inventario.dto.LoginRequest;
import com.app.inventario.dto.LoginResponse;
import com.app.inventario.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
