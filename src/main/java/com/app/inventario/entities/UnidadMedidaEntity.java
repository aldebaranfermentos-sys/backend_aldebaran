package com.app.inventario.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Unidad_medida\"")
@Data @NoArgsConstructor @AllArgsConstructor
public class UnidadMedidaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idUnidad_medida\"")
    private Integer id;

    @Column(name = "\"Nombre\"")
    private String nombre;
}
