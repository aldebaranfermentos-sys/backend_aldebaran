package com.app.inventario.controller;

import com.app.inventario.dto.*;
import com.app.inventario.service.UnidadMedidaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadMedidaController {

    private final UnidadMedidaService service;
    public UnidadMedidaController(UnidadMedidaService service) { this.service = service; }

    @GetMapping public List<UnidadMedidaResponse> listar(){ return service.listar(); }
    @GetMapping("/{id}") public UnidadMedidaResponse obtener(@PathVariable Integer id){ return service.obtener(id); }

    @PostMapping
    public ResponseEntity<UnidadMedidaResponse> crear(@Valid @RequestBody UnidadMedidaRequest req){
        var creado = service.crear(req);
        return ResponseEntity.created(URI.create("/api/unidades/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public UnidadMedidaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody UnidadMedidaRequest req){
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

