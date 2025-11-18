package com.app.inventario.service;

import com.app.inventario.dto.*;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse obtener(Integer id);
    UsuarioResponse crear(UsuarioRequest req);
    UsuarioResponse actualizar(Integer id, UsuarioUpdateRequest req);
    void eliminar(Integer id);
}
