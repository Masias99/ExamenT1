package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String tipo;          // ROPA, CALZADO, ACCESORIO
    private Long categoriaId;

    // Ropa: talla, color
    // Calzado: talla, material
    // Accesorio: tipoAccesorio
    private String talla;
    private String color;
    private String material;
    private String tipoAccesorio;
}
