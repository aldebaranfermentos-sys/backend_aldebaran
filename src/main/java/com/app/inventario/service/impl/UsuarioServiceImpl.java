package com.app.inventario.service.impl;

import com.app.inventario.dto.UsuarioRequest;
import com.app.inventario.dto.UsuarioResponse;
import com.app.inventario.dto.UsuarioUpdateRequest;
import com.app.inventario.entities.UsuarioEntity;
import com.app.inventario.repository.UsuarioRepository;
import com.app.inventario.service.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder; // si no quieres hashing, elimina esto y su uso

    public UsuarioServiceImpl(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repo.findAll().stream().map(this::toRes).toList();
    }

    @Override @Transactional(readOnly = true)
    public UsuarioResponse obtener(Integer id) {
        var e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + id));
        return toRes(e);
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest req) {
        if (req.correo() != null && repo.existsByCorreo(req.correo()))
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado: " + req.correo());

        var e = new UsuarioEntity();
        e.setNombre(req.nombre());
        e.setApellido(req.apellido());
        e.setCorreo(req.correo());
        e.setRol(req.rol());
        // Hash de contraseña
        e.setContrasena(encoder.encode(req.contrasena()));
        return toRes(repo.save(e));
    }

    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioUpdateRequest req) {
        var e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + id));

        if (req.nombre() != null)   e.setNombre(req.nombre());
        if (req.apellido() != null) e.setApellido(req.apellido());
        if (req.correo() != null) {
            if (repo.existsByCorreo(req.correo()) && !req.correo().equalsIgnoreCase(e.getCorreo()))
                throw new ResponseStatusException(CONFLICT, "Correo ya registrado: " + req.correo());
            e.setCorreo(req.correo());
        }
        if (req.rol() != null)      e.setRol(req.rol());
        if (req.contrasena() != null && !req.contrasena().isBlank())
            e.setContrasena(encoder.encode(req.contrasena()));

        return toRes(repo.save(e));
    }

    @Override
    public void eliminar(Integer id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + id);
        repo.deleteById(id);
    }

    private UsuarioResponse toRes(UsuarioEntity e) {
        return new UsuarioResponse(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getCorreo(),
                e.getRol()
        );
    }
}
