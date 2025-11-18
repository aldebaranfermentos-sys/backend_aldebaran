package com.app.inventario.service;

import com.app.inventario.dto.LoginRequest;
import com.app.inventario.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
