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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listar() {
        return ResponseEntity.ok(
                pedidoRepository.findAll().stream().map(p -> toDTO(p, false)).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
        return ResponseEntity.ok(toDTO(pedido, false));
    }


    @GetMapping("/por-estado")
    public ResponseEntity<List<PedidoDTO>> buscarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(
                pedidoRepository.findByEstado(estado).stream().map(p -> toDTO(p, false)).collect(Collectors.toList())
        );
    }


    @GetMapping("/por-cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(
                pedidoRepository.buscarPorCliente(clienteId).stream().map(p -> toDTO(p, false)).collect(Collectors.toList())
        );
    }


    @GetMapping("/{id}/con-detalle-producto")
    public ResponseEntity<PedidoDTO> buscarConDetalleYProducto(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.buscarConDetalleYProducto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
        return ResponseEntity.ok(toDTO(pedido, true));
    }


    @GetMapping("/por-rango-fecha")
    public ResponseEntity<List<PedidoDTO>> buscarPorRangoFecha(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(
                pedidoRepository.buscarPorRangoFecha(inicio, fin).stream().map(p -> toDTO(p, false)).collect(Collectors.toList())
        );
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PedidoDTO> crear(@RequestBody PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + dto.getClienteId()));

        Pedido pedido = Pedido.builder()
                .fechaPedido(LocalDateTime.now())
                .estado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE")
                .cliente(cliente)
                .build();
        pedidoRepository.save(pedido);

        if (dto.getDetalles() != null) {
            for (DetallePedidoRequestDTO det : dto.getDetalles()) {
                Producto producto = productoRepository.findById(det.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + det.getProductoId()));

                DetallePedido detalle = DetallePedido.builder()
                        .pedido(pedido)
                        .producto(producto)
                        .cantidad(det.getCantidad())
                        .precioUnitario(producto.getPrecio())
                        .build();
                detallePedidoRepository.save(detalle);
                pedido.getDetalles().add(detalle);
            }
        }

        entityManager.flush();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(pedido, true));
    }

    @PostMapping("/{id}/detalles")
    @Transactional
    public ResponseEntity<PedidoDTO> agregarDetalle(@PathVariable Long id, @RequestBody DetallePedidoRequestDTO det) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
        Producto producto = productoRepository.findById(det.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + det.getProductoId()));

        DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(det.getCantidad())
                .precioUnitario(producto.getPrecio())
                .build();
        detallePedidoRepository.save(detalle);
        pedido.getDetalles().add(detalle);

        entityManager.flush();

        return ResponseEntity.ok(toDTO(pedido, true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizarEstado(@PathVariable Long id, @RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
        if (dto.getEstado() != null) {
            pedido.setEstado(dto.getEstado());
        }
        return ResponseEntity.ok(toDTO(pedidoRepository.save(pedido), false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado con id " + id);
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    private PedidoDTO toDTO(Pedido p, boolean incluirDetalles) {
        PedidoDTO.PedidoDTOBuilder builder = PedidoDTO.builder()
                .id(p.getId())
                .fechaPedido(p.getFechaPedido())
                .estado(p.getEstado())
                .clienteId(p.getCliente().getId())
                .clienteNombreCompleto(p.getCliente().getNombres() + " " + p.getCliente().getApellidos());

        if (incluirDetalles) {
            builder.detalles(p.getDetalles().stream().map(d -> DetallePedidoDTO.builder()
                    .id(d.getId())
                    .productoId(d.getProducto().getId())
                    .productoNombre(d.getProducto().getNombre())
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .build()).collect(Collectors.toList()));
        }

        return builder.build();
    }
}
