package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String tipo;          // ROPA, CALZADO, ACCESORIO
    private Long categoriaId;
    private String categoriaNombre;

    // Ropa / Calzado
    private String talla;

    // Ropa
    private String color;

    // Calzado
    private String material;

    // Accesorio
    private String tipoAccesorio;
}
