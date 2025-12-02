package com.app.inventario.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "equipos")
public class EquipoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado; // operativo, mantenimiento, inactivo

    @Column(name = "ubicacion", length = 200)
    private String ubicacion;

    @Column(name = "observacion", length = 500)
    private String observacion;
}