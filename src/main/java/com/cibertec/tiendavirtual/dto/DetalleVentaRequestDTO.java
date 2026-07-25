package com.cibertec.tiendavirtual.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaRequestDTO {
    private Long productoId;
    private Integer cantidad;
}
