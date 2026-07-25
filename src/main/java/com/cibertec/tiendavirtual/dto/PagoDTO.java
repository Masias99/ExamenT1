package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {
    private Long id;
    private Long ventaId;
    private String metodoPago; // TARJETA, YAPE, PLIN, EFECTIVO
    private BigDecimal monto;
    private LocalDateTime fechaPago;
}
