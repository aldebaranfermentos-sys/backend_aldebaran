package com.app.inventario.service;

import com.app.inventario.dto.EquipoRequest;
import com.app.inventario.dto.EquipoResponse;

import java.util.List;

public interface EquipoService {
    List<EquipoResponse> listar();
    EquipoResponse obtenerPorId(Long id);
    EquipoResponse crear(EquipoRequest request);
    EquipoResponse actualizar(Long id, EquipoRequest request);
    void eliminar(Long id);
    List<EquipoResponse> listarPorEstado(String estado);
}