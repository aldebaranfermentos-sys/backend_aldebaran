package com.app.inventario.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "\"Insumos\"") // la tabla existe como "Insumos"
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idInsumos\"")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "\"Nombre\"")
    private String nombre;

    @Size(max = 500)
    @Column(name = "\"Descripcion\"")
    private String descripcion;

    @Size(max = 100)
    @Column(name = "\"Unidad_de_medida\"")
    private String unidadMedida;

    @Min(0)
    @Column(name = "\"Stock_minimo\"")
    private Integer stockMinimo;

    @Column(name = "\"Categoria_idCategoria\"")
    private Integer categoriaId;

    @Column(name = "\"Unidad_medida_idUnidad_medida\"")
    private Integer unidadMedidaId;
}
