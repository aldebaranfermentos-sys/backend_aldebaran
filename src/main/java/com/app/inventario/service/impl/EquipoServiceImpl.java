package com.app.inventario.service.impl;

import com.app.inventario.dto.EquipoRequest;
import com.app.inventario.dto.EquipoResponse;
import com.app.inventario.entities.EquipoEntity;
import com.app.inventario.repository.EquipoRepository;
import com.app.inventario.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponse obtenerPorId(Long id) {
        EquipoEntity entity = equipoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Equipo no encontrado"));
        return toResponse(entity);
    }

    @Override
    public EquipoResponse crear(EquipoRequest request) {
        EquipoEntity entity = EquipoEntity.builder()
                .nombre(request.nombre())
                .estado(request.estado())
                .ubicacion(request.ubicacion())
                .observacion(request.observacion())
                .build();

        EquipoEntity guardado = equipoRepository.save(entity);
        return toResponse(guardado);
    }

    @Override
    public EquipoResponse actualizar(Long id, EquipoRequest request) {
        EquipoEntity entity = equipoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Equipo no encontrado"));

        entity.setNombre(request.nombre());
        entity.setEstado(request.estado());
        entity.setUbicacion(request.ubicacion());
        entity.setObservacion(request.observacion());

        EquipoEntity guardado = equipoRepository.save(entity);
        return toResponse(guardado);
    }

    @Override
    public void eliminar(Long id) {
        if (!equipoRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Equipo no encontrado con ID: " + id);
        }

        equipoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponse> listarPorEstado(String estado) {
        return equipoRepository.findByEstado(estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private EquipoResponse toResponse(EquipoEntity e) {
        return new EquipoResponse(
                e.getId(),
                e.getNombre(),
                e.getEstado(),
                e.getUbicacion(),
                e.getObservacion()
        );
    }
}