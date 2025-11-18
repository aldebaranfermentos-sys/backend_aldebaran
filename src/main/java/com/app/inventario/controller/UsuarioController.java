package com.app.inventario.controller;

import com.app.inventario.dto.UsuarioRequest;
import com.app.inventario.dto.UsuarioResponse;
import com.app.inventario.dto.UsuarioUpdateRequest;
import com.app.inventario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest req) {
        var creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
