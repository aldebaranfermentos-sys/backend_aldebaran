package com.app.inventario.service;

import com.app.inventario.dto.CategoriaRequest;
import com.app.inventario.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponse> listar();
    CategoriaResponse obtener(Integer id);
    CategoriaResponse crear(CategoriaRequest req);
    CategoriaResponse actualizar(Integer id, CategoriaRequest req);
    void eliminar(Integer id);
}
