package com.app.inventario.service.impl;

import com.app.inventario.dto.ProductoRequest;
import com.app.inventario.dto.ProductoResponse;
import com.app.inventario.entities.ProductoEntity;
import com.app.inventario.repository.ProductoRepository;
import com.app.inventario.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repo;

    public ProductoServiceImpl(ProductoRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        var e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        return toResponse(e);
    }

    @Override
    public ProductoResponse crear(ProductoRequest r) {
        var e = new ProductoEntity();
        apply(e, r);
        return toResponse(repo.save(e));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest r) {
        var e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        apply(e, r);
        return toResponse(repo.save(e));
    }

    @Override
    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Producto no encontrado: " + id);
        repo.deleteById(id);
    }

    private void apply(ProductoEntity e, ProductoRequest r) {
        e.setNombre(r.nombre());
        e.setDescripcion(r.descripcion());
        e.setUnidadMedida(r.unidadMedida());
        e.setStockMinimo(r.stockMinimo());
        e.setCategoriaId(r.categoriaId());
        e.setUnidadMedidaId(r.unidadMedidaId());
    }

    private ProductoResponse toResponse(ProductoEntity e) {
        return new ProductoResponse(
                e.getId(), e.getNombre(), e.getDescripcion(), e.getUnidadMedida(),
                e.getStockMinimo(), e.getCategoriaId(), e.getUnidadMedidaId()
        );
    }
}
