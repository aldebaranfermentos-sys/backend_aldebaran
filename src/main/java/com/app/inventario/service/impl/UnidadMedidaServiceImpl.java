package com.app.inventario.service.impl;

import com.app.inventario.dto.*;
import com.app.inventario.entities.UnidadMedidaEntity;
import com.app.inventario.repository.UnidadMedidaRepository;
import com.app.inventario.service.UnidadMedidaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    private final UnidadMedidaRepository repo;

    public UnidadMedidaServiceImpl(UnidadMedidaRepository repo) { this.repo = repo; }

    @Override @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> listar() {
        return repo.findAll().stream().map(this::toRes).toList();
    }

    @Override @Transactional(readOnly = true)
    public UnidadMedidaResponse obtener(Integer id) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + id));
        return toRes(e);
    }

    @Override
    public UnidadMedidaResponse crear(UnidadMedidaRequest req) {
        var e = new UnidadMedidaEntity();
        e.setNombre(req.nombre());
        return toRes(repo.save(e));
    }

    @Override
    public UnidadMedidaResponse actualizar(Integer id, UnidadMedidaRequest req) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + id));
        e.setNombre(req.nombre());
        return toRes(repo.save(e));
    }

    @Override
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Unidad no encontrada: " + id);
        repo.deleteById(id);
    }

    private UnidadMedidaResponse toRes(UnidadMedidaEntity e) { return new UnidadMedidaResponse(e.getId(), e.getNombre()); }
}
