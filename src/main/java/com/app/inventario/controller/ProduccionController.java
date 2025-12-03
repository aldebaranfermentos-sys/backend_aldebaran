package com.app.inventario.controller;

import com.app.inventario.dto.DetalleProduccionRequest;
import com.app.inventario.dto.ProduccionRequest;
import com.app.inventario.dto.ProduccionResponse;
import com.app.inventario.service.ProduccionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/produccion")
public class ProduccionController {

    private final ProduccionService produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    // ============================
    // LISTAR SIMPLE
    // ============================
    @GetMapping
    public List<ProduccionResponse> listar() {
        return produccionService.listar();
    }

    // ============================
    // LISTAR CON FILTROS
    // ============================
    @GetMapping("/filtro")
    public ResponseEntity<Page<ProduccionResponse>> filtrar(
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Long usuarioResponsableId,
            @PageableDefault(size = 20, sort = "fechaProduccion") Pageable pageable
    ) {
        Page<ProduccionResponse> pagina = produccionService.filtrar(
                orden, estado, fechaDesde, fechaHasta, usuarioResponsableId, pageable);
        return ResponseEntity.ok(pagina);
    }

    // ============================
    // OBTENER POR ID
    // ============================
    @GetMapping("/{id}")
    public ProduccionResponse obtener(@PathVariable Long id) {
        return produccionService.obtenerPorId(id);
    }

    // ============================
    // CREAR
    // ============================
    @PostMapping
    public ResponseEntity<ProduccionResponse> crear(
            @Valid @RequestBody ProduccionRequest request) {

        ProduccionResponse creada = produccionService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/produccion/" + creada.id()))
                .body(creada);
    }

    // ============================
    // ACTUALIZAR
    // ============================
    @PutMapping("/{id}")
    public ProduccionResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProduccionRequest request) {

        return produccionService.actualizar(id, request);
    }

    // ============================
    // ELIMINAR
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        produccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // COMPLETAR PRODUCCIÓN
    // ============================
    @PostMapping("/{id}/completar")
    public ProduccionResponse completar(@PathVariable Long id) {
        return produccionService.completarProduccion(id);
    }

    // ============================
    // DETALLES / RECETAS
    // ============================
    @PostMapping("/{id}/detalles/desde-receta/{recetaId}")
    public ProduccionResponse agregarDetallesDesdeReceta(
            @PathVariable Long id,
            @PathVariable Long recetaId) {

        return produccionService.agregarDetallesDesdeReceta(id, recetaId);
    }

    @PostMapping("/{id}/detalles")
    public ProduccionResponse agregarDetalleProducto(
            @PathVariable Long id,
            @Valid @RequestBody DetalleProduccionRequest detalle) {

        return produccionService.agregarDetalleProducto(id, detalle);
    }

    @DeleteMapping("/detalles/{detalleId}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long detalleId) {
        produccionService.eliminarDetalle(detalleId);
        return ResponseEntity.noContent().build();
    }
}
