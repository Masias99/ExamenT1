package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDTO {
    private Long id;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private Long clienteId;
    private String clienteNombreCompleto;

    @Builder.Default
    private List<DetalleVentaDTO> detalles = new ArrayList<>();

    private PagoDTO pago;
}
