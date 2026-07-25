package com.cibertec.tiendavirtual.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaRequestDTO {
    private Long clienteId;

    @Builder.Default
    private List<DetalleVentaRequestDTO> detalles = new ArrayList<>();
}
