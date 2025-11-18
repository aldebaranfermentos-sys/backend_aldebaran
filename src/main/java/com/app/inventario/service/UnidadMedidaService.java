package com.app.inventario.service;

import com.app.inventario.dto.UnidadMedidaRequest;
import com.app.inventario.dto.UnidadMedidaResponse;

import java.util.List;

public interface UnidadMedidaService {
    List<UnidadMedidaResponse> listar();
    UnidadMedidaResponse obtener(Integer id);
    UnidadMedidaResponse crear(UnidadMedidaRequest req);
    UnidadMedidaResponse actualizar(Integer id, UnidadMedidaRequest req);
    void eliminar(Integer id);
}
