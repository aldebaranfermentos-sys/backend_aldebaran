package com.app.inventario.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Categoria\"")
@Data @NoArgsConstructor @AllArgsConstructor
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idCategoria\"")
    private Integer id;

    @Column(name = "\"Nombre\"")
    private String nombre;

    @Column(name="\"Descripcion\"")
    private String descripcion;

}
