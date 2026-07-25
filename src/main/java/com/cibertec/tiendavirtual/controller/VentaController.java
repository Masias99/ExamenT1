package com.cibertec.tiendavirtual.controller;

import com.cibertec.tiendavirtual.dto.*;
import com.cibertec.tiendavirtual.exception.ResourceNotFoundException;
import com.cibertec.tiendavirtual.model.*;
import com.cibertec.tiendavirtual.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PagoRepository pagoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar() {
        return ResponseEntity.ok(
                ventaRepository.findAll().stream().map(v -> toDTO(v, false, false)).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerPorId(@PathVariable Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
        return ResponseEntity.ok(toDTO(venta, false, false));
    }

    // Derived query
    @GetMapping("/por-cliente/{clienteId}")
    public ResponseEntity<List<VentaDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(
                ventaRepository.findByClienteId(clienteId).stream().map(v -> toDTO(v, false, false)).collect(Collectors.toList())
        );
    }


    @GetMapping("/total-por-cliente")
    public ResponseEntity<List<Object[]>> totalVendidoPorCliente() {
        return ResponseEntity.ok(ventaRepository.totalVendidoPorCliente());
    }


    @GetMapping("/{id}/con-pago")
    public ResponseEntity<VentaDTO> buscarConPago(@PathVariable Long id) {
        Venta venta = ventaRepository.buscarConPagoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
        return ResponseEntity.ok(toDTO(venta, false, true));
    }


    @GetMapping("/{id}/con-detalle-producto")
    public ResponseEntity<VentaDTO> buscarConDetalleYProducto(@PathVariable Long id) {
        Venta venta = ventaRepository.buscarConDetalleYProductoPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
        return ResponseEntity.ok(toDTO(venta, true, false));
    }


    @GetMapping("/por-fecha-monto")
    public ResponseEntity<List<VentaDTO>> buscarPorFechaYMontoMinimo(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam BigDecimal montoMinimo) {
        return ResponseEntity.ok(
                ventaRepository.buscarPorFechaYMontoMinimo(inicio, fin, montoMinimo)
                        .stream().map(v -> toDTO(v, false, false)).collect(Collectors.toList())
        );
    }


    @PostMapping
    @Transactional
    public ResponseEntity<VentaDTO> crear(@RequestBody VentaRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + dto.getClienteId()));

        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .cliente(cliente)
                .build();
        ventaRepository.save(venta);

        BigDecimal total = BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (DetalleVentaRequestDTO det : dto.getDetalles()) {
                Producto producto = productoRepository.findById(det.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + det.getProductoId()));

                DetalleVenta detalle = DetalleVenta.builder()
                        .venta(venta)
                        .producto(producto)
                        .cantidad(det.getCantidad())
                        .precioUnitario(producto.getPrecio())
                        .build();
                detalleVentaRepository.save(detalle);
                venta.getDetalles().add(detalle);

                total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(det.getCantidad())));
            }
        }
        venta.setTotal(total);
        ventaRepository.save(venta);

        entityManager.flush();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(venta, true, false));
    }


    @PostMapping("/{id}/pago")
    @Transactional
    public ResponseEntity<VentaDTO> registrarPago(@PathVariable Long id, @RequestBody PagoRequestDTO dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));

        Pago pago = Pago.builder()
                .venta(venta)
                .metodoPago(dto.getMetodoPago())
                .monto(dto.getMonto() != null ? dto.getMonto() : venta.getTotal())
                .fechaPago(LocalDateTime.now())
                .build();
        pagoRepository.save(pago);
        venta.setPago(pago);

        entityManager.flush();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(venta, false, true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta no encontrada con id " + id);
        }
        ventaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    private VentaDTO toDTO(Venta v, boolean incluirDetalles, boolean incluirPago) {
        VentaDTO.VentaDTOBuilder builder = VentaDTO.builder()
                .id(v.getId())
                .fechaVenta(v.getFechaVenta())
                .total(v.getTotal())
                .clienteId(v.getCliente().getId())
                .clienteNombreCompleto(v.getCliente().getNombres() + " " + v.getCliente().getApellidos());

        if (incluirDetalles) {
            builder.detalles(v.getDetalles().stream().map(d -> DetalleVentaDTO.builder()
                    .id(d.getId())
                    .productoId(d.getProducto().getId())
                    .productoNombre(d.getProducto().getNombre())
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .build()).collect(Collectors.toList()));
        }

        if (incluirPago && v.getPago() != null) {
            Pago p = v.getPago();
            builder.pago(PagoDTO.builder()
                    .id(p.getId())
                    .ventaId(v.getId())
                    .metodoPago(p.getMetodoPago())
                    .monto(p.getMonto())
                    .fechaPago(p.getFechaPago())
                    .build());
        }

        return builder.build();
    }
}
