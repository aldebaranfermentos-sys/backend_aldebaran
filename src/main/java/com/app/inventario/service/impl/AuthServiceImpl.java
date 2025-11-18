package com.app.inventario.service.impl;

import com.app.inventario.dto.LoginRequest;
import com.app.inventario.dto.LoginResponse;
import com.app.inventario.entities.UsuarioEntity;
import com.app.inventario.repository.UsuarioRepository;
import com.app.inventario.security.JwtService;
import com.app.inventario.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        UsuarioEntity user = usuarioRepository.findByCorreo(request.email())
                .orElseThrow(() ->
                        new ResponseStatusException(UNAUTHORIZED, "Correo o contraseña inválidos")
                );

        if (!passwordEncoder.matches(request.password(), user.getContrasena())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Correo o contraseña inválidos");
        }

        String token = jwtService.generate(
                user.getCorreo(), // subject
                Map.of(
                        "id", String.valueOf(user.getId()),
                        "email", user.getCorreo(),
                        "nombre", user.getNombre(),
                        "rol", user.getRol() != null ? user.getRol() : ""
                )
        );

        return new LoginResponse(
                user.getId(),
                user.getNombre(),
                user.getCorreo(),
                user.getRol(),
                token
        );
    }
}
