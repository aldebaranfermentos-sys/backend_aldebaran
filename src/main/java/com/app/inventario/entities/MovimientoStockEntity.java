package com.app.inventario.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fechamovimiento")  // ✅ Sin comillas, minúsculas
    private LocalDateTime fechaMovimiento;

    @Column(name = "stockantes")  // ✅ Sin comillas, minúsculas
    private Integer stockAntes;

    @Column(name = "stockdespues")  // ✅ Sin comillas, minúsculas
    private Integer stockDespues;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoMovimientoStock tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockEntity stock;
}
