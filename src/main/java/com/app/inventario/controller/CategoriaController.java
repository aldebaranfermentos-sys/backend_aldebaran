package com.app.inventario.controller;

import com.app.inventario.dto.*;
import com.app.inventario.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;
    public CategoriaController(CategoriaService service) { this.service = service; }

    @GetMapping public List<CategoriaResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public CategoriaResponse obtener(@PathVariable Integer id){ return service.obtener(id); }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest req) {
        var creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/categorias/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}") public CategoriaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaRequest req){
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
