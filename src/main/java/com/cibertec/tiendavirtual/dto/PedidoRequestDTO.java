package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {
    private Long clienteId;
    private String estado; // PENDIENTE, CONFIRMADO, CANCELADO

    @Builder.Default
    private List<DetallePedidoRequestDTO> detalles = new ArrayList<>();
}
