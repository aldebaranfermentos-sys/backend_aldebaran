package com.app.inventario.service.impl;

import com.app.inventario.dto.*;
import com.app.inventario.entities.*;
import com.app.inventario.repository.*;
import com.app.inventario.service.ProduccionService;
import com.app.inventario.spec.ProduccionSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduccionServiceImpl implements ProduccionService {

    private final ProduccionRepository produccionRepository;
    private final DetalleProduccionRepository detalleProduccionRepository;
    private final ProductoRepository productoRepository;
    private final StockRepository stockRepository;
    private final UsuarioRepository userRepository;
    private final RecetaRepository recetaRepository;
    private final MovimientoStockRepository movimientoStockRepository;


    // ============================================================
    // LISTAR
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public List<ProduccionResponse> listar() {
        return produccionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // OBTENER POR ID
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public ProduccionResponse obtenerPorId(Long id) {
        ProduccionEntity entity = produccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));
        return toResponse(entity);
    }

    // ============================================================
    // CREAR
    // ============================================================
    @Override
    public ProduccionResponse crear(ProduccionRequest request) {
        if (produccionRepository.findByOrden(request.orden()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Orden duplicada");
        }

        UsuarioEntity usuario = null;
        if (request.usuarioResponsableId() != null) {
            usuario = userRepository.findById(request.usuarioResponsableId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        }

        ProduccionEntity produccion = ProduccionEntity.builder()
                .orden(request.orden())
                .cantidadAProducir(request.cantidadAProducir())
                .fechaProduccion(request.fechaProduccion() != null ? request.fechaProduccion() : LocalDate.now())
                .estado(request.estado())
                .usuarioResponsable(usuario)
                .observacion(request.observacion())
                .build();

        if (request.detalles() != null && !request.detalles().isEmpty()) {
            validarStockDisponible(request.detalles());

            for (DetalleProduccionRequest detalleReq : request.detalles()) {
                ProductoEntity producto = productoRepository.findById(detalleReq.productoId())
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));

                DetalleProduccionEntity detalle = DetalleProduccionEntity.builder()
                        .produccion(produccion)
                        .producto(producto)
                        .cantidadUtilizada(detalleReq.cantidadUtilizada())
                        .observacion(detalleReq.observacion())
                        .build();

                produccion.getDetalles().add(detalle);
            }
        }

        ProduccionEntity guardada = produccionRepository.save(produccion);
        return toResponse(guardada);
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    @Override
    public ProduccionResponse actualizar(Long id, ProduccionRequest request) {
        ProduccionEntity entity = produccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));

        if (!"Pendiente".equals(entity.getEstado()) && !"En Proceso".equals(entity.getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Solo se pueden actualizar producciones en estado Pendiente o En Proceso");
        }

        validarStockDisponible(request.detalles());

        entity.setOrden(request.orden());
        entity.setCantidadAProducir(request.cantidadAProducir());
        entity.setFechaProduccion(request.fechaProduccion());
        entity.setEstado(request.estado());
        entity.setObservacion(request.observacion());

        if (request.usuarioResponsableId() != null) {
            UsuarioEntity usuario = userRepository.findById(request.usuarioResponsableId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
            entity.setUsuarioResponsable(usuario);
        }

        entity.getDetalles().clear();

        for (DetalleProduccionRequest detalleReq : request.detalles()) {
            ProductoEntity producto = productoRepository.findById(detalleReq.productoId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                            "Producto no encontrado: " + detalleReq.productoId()));

            DetalleProduccionEntity detalle = DetalleProduccionEntity.builder()
                    .produccion(entity)
                    .producto(producto)
                    .cantidadUtilizada(detalleReq.cantidadUtilizada())
                    .observacion(detalleReq.observacion())
                    .build();

            entity.getDetalles().add(detalle);
        }

        ProduccionEntity guardada = produccionRepository.save(entity);
        return toResponse(guardada);
    }

    // ============================================================
    // ELIMINAR
    // ============================================================
    @Override
    public void eliminar(Long id) {
        ProduccionEntity entity = produccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));

        if ("Completada".equals(entity.getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se pueden eliminar producciones completadas porque ya descargaron el stock");
        }

        produccionRepository.deleteById(id);
    }

    // ============================================================
    // COMPLETAR PRODUCCIÓN
    // ============================================================
    @Override
    public ProduccionResponse completarProduccion(Long id) {
        ProduccionEntity entity = produccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));

        if (!"Pendiente".equals(entity.getEstado()) && !"En Proceso".equals(entity.getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Solo se pueden completar producciones en estado Pendiente o En Proceso");
        }

        // Convertir detalles para validación
        List<DetalleProduccionRequest> detalles = entity.getDetalles().stream()
                .map(d -> new DetalleProduccionRequest(
                        d.getProducto().getId(),
                        d.getCantidadUtilizada(),
                        d.getObservacion()
                ))
                .toList();

        validarStockDisponible(detalles);

        // 🚀 FEFO: Consumir stock por lote (el más antiguo primero)
        for (DetalleProduccionEntity detalle : entity.getDetalles()) {

            List<StockEntity> lotes = stockRepository
                    .findByInsumo_IdOrderByLoteProveedor_FechaVencimientoAsc(detalle.getProducto().getId());

            int restante = detalle.getCantidadUtilizada();

            for (StockEntity lote : lotes) {

                if (restante <= 0) break;

                int disponible = lote.getCantidadActual();
                int aDescontar = Math.min(disponible, restante);

                int antes = lote.getCantidadActual();
                int despues = antes - aDescontar;

                lote.setCantidadActual(despues);
                lote.setFechaActualizacion(LocalDate.now());
                stockRepository.save(lote);

                // Registrar movimiento por lote
                MovimientoStockEntity mov = MovimientoStockEntity.builder()
                        .stock(lote)
                        .tipo(TipoMovimientoStock.SALIDA)
                        .cantidad(aDescontar)
                        .stockAntes(antes)
                        .stockDespues(despues)
                        .motivo("Salida por producción (lote " +
                                (lote.getLoteProveedor() != null ? lote.getLoteProveedor().getNumeroLote() : "N/A") +
                                ")")
                        .fechaMovimiento(LocalDateTime.now())
                        .build();

                movimientoStockRepository.save(mov);

                restante -= aDescontar;
            }

            if (restante > 0) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Stock insuficiente por lotes para el producto: " + detalle.getProducto().getNombre());
            }
        }

        entity.setEstado("Completada");
        ProduccionEntity guardada = produccionRepository.save(entity);
        return toResponse(guardada);
    }


    // ============================================================
    // FILTRAR
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Page<ProduccionResponse> filtrar(String orden,
                                            String estado,
                                            LocalDate fechaDesde,
                                            LocalDate fechaHasta,
                                            Long usuarioResponsableId,
                                            Pageable pageable) {

        Specification<ProduccionEntity> spec = (root, query, cb) -> null;

        spec = spec
                .and(ProduccionSpecifications.ordenContiene(orden))
                .and(ProduccionSpecifications.estadoEs(estado))
                .and(ProduccionSpecifications.fechaDesde(fechaDesde))
                .and(ProduccionSpecifications.fechaHasta(fechaHasta))
                .and(ProduccionSpecifications.usuarioResponsableId(usuarioResponsableId));

        return produccionRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    // ============================================================
    // RECETAS Y DETALLES
    // ============================================================
    @Override
    public ProduccionResponse agregarDetallesDesdeReceta(Long produccionId, Long recetaId) {
        ProduccionEntity produccion = produccionRepository.findById(produccionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));

        if (!"Pendiente".equals(produccion.getEstado()) && !"En Proceso".equals(produccion.getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Solo se puede modificar producción en estado Pendiente o En Proceso");
        }

        RecetaEntity receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Receta no encontrada"));

        for (DetalleRecetaEntity ingrediente : receta.getIngredientes()) {
            DetalleProduccionEntity detalle = DetalleProduccionEntity.builder()
                    .produccion(produccion)
                    .producto(ingrediente.getInsumo())
                    .cantidadUtilizada(ingrediente.getCantidadRequerida())
                    .observacion("Desde receta: " + receta.getNombre())
                    .build();

            produccion.getDetalles().add(detalle);
        }

        List<DetalleProduccionRequest> detallesValidar = produccion.getDetalles().stream()
                .map(d -> new DetalleProduccionRequest(
                        d.getProducto().getId(),
                        d.getCantidadUtilizada(),
                        d.getObservacion()
                )).toList();

        validarStockDisponible(detallesValidar);

        ProduccionEntity guardada = produccionRepository.save(produccion);
        return toResponse(guardada);
    }

    @Override
    public ProduccionResponse agregarDetalleProducto(Long produccionId, DetalleProduccionRequest detalle) {
        ProduccionEntity produccion = produccionRepository.findById(produccionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producción no encontrada"));

        if (!"Pendiente".equals(produccion.getEstado()) && !"En Proceso".equals(produccion.getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Solo se puede modificar producción en estado Pendiente o En Proceso");
        }

        ProductoEntity producto = productoRepository.findById(detalle.productoId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));

        validarStockDisponible(List.of(detalle));

        DetalleProduccionEntity nuevoDetalle = DetalleProduccionEntity.builder()
                .produccion(produccion)
                .producto(producto)
                .cantidadUtilizada(detalle.cantidadUtilizada())
                .observacion(detalle.observacion())
                .build();

        produccion.getDetalles().add(nuevoDetalle);

        ProduccionEntity guardada = produccionRepository.save(produccion);
        return toResponse(guardada);
    }

    @Override
    public void eliminarDetalle(Long detalleId) {
        DetalleProduccionEntity detalle = detalleProduccionRepository.findById(detalleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Detalle no encontrado"));

        if (!"Pendiente".equals(detalle.getProduccion().getEstado())
                && !"En Proceso".equals(detalle.getProduccion().getEstado())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se puede modificar producción que no está en Pendiente o En Proceso");
        }

        detalleProduccionRepository.delete(detalle);
    }

    // ============================================================
    // VALIDAR STOCK
    // ============================================================
    private void validarStockDisponible(List<DetalleProduccionRequest> detalles) {

        StringBuilder errores = new StringBuilder();

        for (DetalleProduccionRequest detalle : detalles) {

            ProductoEntity producto = productoRepository.findById(detalle.productoId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                            "Producto no encontrado: " + detalle.productoId()));

            List<StockEntity> lotes = stockRepository
                    .findByInsumo_IdOrderByLoteProveedor_FechaVencimientoAsc(detalle.productoId());

            if (lotes.isEmpty()) {
                errores.append("- El producto '")
                        .append(producto.getNombre())
                        .append("' no tiene stock registrado.\n");
                continue;
            }

            int totalDisponible = lotes.stream()
                    .mapToInt(StockEntity::getCantidadActual)
                    .sum();

            if (totalDisponible < detalle.cantidadUtilizada()) {
                errores.append(String.format(
                        "- El producto '%s' no tiene suficiente stock. Disponible total: %d, Requerido: %d.\n",
                        producto.getNombre(),
                        totalDisponible,
                        detalle.cantidadUtilizada()
                ));
            }
        }

        if (errores.length() > 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No hay suficiente stock:\n" + errores);
        }
    }


    // ============================================================
    // MAPEADOR
    // ============================================================
    private ProduccionResponse toResponse(ProduccionEntity e) {
        List<DetalleProduccionResponse> detalles = e.getDetalles().stream()
                .map(d -> new DetalleProduccionResponse(
                        d.getId(),
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidadUtilizada(),
                        d.getObservacion()
                ))
                .collect(Collectors.toList());

        return new ProduccionResponse(
                e.getId(),
                e.getOrden(),
                e.getCantidadAProducir(),
                e.getFechaProduccion(),
                e.getEstado(),
                e.getUsuarioResponsable() != null ? e.getUsuarioResponsable().getId() : null,
                e.getUsuarioResponsable() != null ? e.getUsuarioResponsable().getNombre() : null,
                e.getObservacion(),
                detalles
        );
    }
}
