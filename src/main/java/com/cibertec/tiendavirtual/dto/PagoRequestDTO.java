package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {
    private String metodoPago; // TARJETA, YAPE, PLIN, EFECTIVO
    private BigDecimal monto;
}
