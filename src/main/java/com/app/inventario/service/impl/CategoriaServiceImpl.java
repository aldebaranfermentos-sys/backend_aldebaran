package com.app.inventario.service.impl;

import com.app.inventario.dto.*;
import com.app.inventario.entities.CategoriaEntity;
import com.app.inventario.repository.CategoriaRepository;
import com.app.inventario.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaServiceImpl(CategoriaRepository repo) { this.repo = repo; }

    @Override @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repo.findAll().stream().map(this::toRes).toList();
    }

    @Override @Transactional(readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        return toRes(e);
    }

    @Override
    public CategoriaResponse crear(CategoriaRequest req) {
        var e = new CategoriaEntity();
        e.setNombre(req.nombre());
        e.setDescripcion(req.descripcion());

        return toRes(repo.save(e));
    }

    @Override
    public CategoriaResponse actualizar(Integer id, CategoriaRequest req) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
        e.setNombre(req.nombre());
        e.setDescripcion(req.descripcion());

        return toRes(repo.save(e));
    }

    @Override
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Categoría no encontrada: " + id);
        repo.deleteById(id);
    }

    private CategoriaResponse toRes(CategoriaEntity e) { return new CategoriaResponse(e.getId(), e.getNombre() , e.getDescripcion()); }
}
