package com.app.inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "\"Stock\"")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idStock\"")
    private Integer id;

    @Column(name = "\"Cantidad_actual\"", nullable = false)
    private Integer cantidadActual;

    @Column(name = "\"Fecha_actualizacion\"")
    private LocalDate fechaActualizacion;

    @Column(name = "\"Ubicacion\"", length = 45)
    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"Insumos_idInsumos\"", nullable = false)
    private ProductoEntity insumo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_proveedor_id")
    private LoteProveedorEntity loteProveedor;
}