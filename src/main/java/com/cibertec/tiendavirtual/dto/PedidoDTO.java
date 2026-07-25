package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private Long clienteId;
    private String clienteNombreCompleto;

    @Builder.Default
    private List<DetallePedidoDTO> detalles = new ArrayList<>();
}
