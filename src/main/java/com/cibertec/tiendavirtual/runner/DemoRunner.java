package com.cibertec.tiendavirtual.runner;

import com.cibertec.tiendavirtual.model.*;
import com.cibertec.tiendavirtual.repository.*;
import com.cibertec.tiendavirtual.service.ProductoFlushService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoFlushService productoFlushService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("\n================ DEMO TIENDA VIRTUAL ================\n");


        Categoria catRopa = categoriaRepository.findByNombre("Ropa de Hombre");
        Categoria catCalzado = categoriaRepository.findByNombre("Calzado Deportivo");
        Categoria catAccesorio = categoriaRepository.findByNombre("Accesorios");


        Ropa poloNuevo = Ropa.builder()
                .nombre("Camisa Casual")
                .precio(BigDecimal.valueOf(69.90))
                .stock(30)
                .categoria(catRopa)
                .talla("L")
                .color("Blanco")
                .build();

        Calzado zapatillaNueva = Calzado.builder()
                .nombre("Zapatilla Urbana Z2")
                .precio(BigDecimal.valueOf(179.90))
                .stock(15)
                .categoria(catCalzado)
                .talla("41")
                .material("Cuero sintetico")
                .build();

        Accesorio lentesNuevos = Accesorio.builder()
                .nombre("Lentes de Sol")
                .precio(BigDecimal.valueOf(59.90))
                .stock(25)
                .categoria(catAccesorio)
                .tipoAccesorio("Lentes")
                .build();

        productoRepository.save(poloNuevo);
        productoRepository.save(zapatillaNueva);
        productoRepository.save(lentesNuevos);


        productoFlushService.registrarLoteDeRopa(catRopa, 5);
        System.out.println(">> Lote de ropa registrado con flush/batch.");


        Cliente cliente = Cliente.builder()
                .nombres("Carlos")
                .apellidos("Ramirez Vega")
                .email("carlos.ramirez" + System.currentTimeMillis() + "@mail.com")
                .telefono("999888777")
                .direccion("Av. La Marina 890")
                .build();
        clienteRepository.save(cliente);


        Pedido pedido = Pedido.builder()
                .fechaPedido(LocalDateTime.now())
                .estado("PENDIENTE")
                .cliente(cliente)
                .build();
        pedidoRepository.save(pedido);

        DetallePedido detPedido = DetallePedido.builder()
                .pedido(pedido)
                .producto(poloNuevo)
                .cantidad(2)
                .precioUnitario(poloNuevo.getPrecio())
                .build();
        detallePedidoRepository.save(detPedido);
        pedido.getDetalles().add(detPedido);


        entityManager.flush();


        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .total(zapatillaNueva.getPrecio())
                .cliente(cliente)
                .build();
        ventaRepository.save(venta);

        DetalleVenta detVenta = DetalleVenta.builder()
                .venta(venta)
                .producto(zapatillaNueva)
                .cantidad(1)
                .precioUnitario(zapatillaNueva.getPrecio())
                .build();
        detalleVentaRepository.save(detVenta);
        venta.getDetalles().add(detVenta); // sincroniza el lado inverso

        Pago pago = Pago.builder()
                .venta(venta)
                .metodoPago("YAPE")
                .monto(venta.getTotal())
                .fechaPago(LocalDateTime.now())
                .build();
        pagoRepository.save(pago);
        venta.setPago(pago); // sincroniza el lado inverso

        entityManager.flush();


        System.out.println("\n--- Fetch LAZY por defecto (categoria de un producto) ---");
        Producto p = productoRepository.findById(poloNuevo.getId()).orElseThrow();
        System.out.println("Producto: " + p.getNombre() + " (categoria se cargara solo al acceder)");
        System.out.println("Categoria: " + p.getCategoria().getNombre());

        System.out.println("\n--- Fetch EAGER forzado con JOIN FETCH (JPQL) ---");
        List<Producto> conStock = productoRepository.buscarConStockDisponibleConCategoria(0);
        conStock.forEach(prod ->
                System.out.println(prod.getNombre() + " -> " + prod.getCategoria().getNombre()));

        System.out.println("\n--- Pedido con detalle y producto cargados en una sola consulta (JOIN FETCH) ---");
        Optional<Pedido> pedidoCompleto = pedidoRepository.buscarConDetalleYProducto(pedido.getId());
        pedidoCompleto.ifPresent(ped ->
                ped.getDetalles().forEach(d ->
                        System.out.println("Detalle: " + d.getProducto().getNombre() + " x" + d.getCantidad())));

        System.out.println("\n--- Venta completa (venta + pago + detalle + producto) con JOIN FETCH ---");
        ventaRepository.buscarConPagoPorId(venta.getId()).ifPresent(v -> {
            System.out.println("Venta total: " + v.getTotal());
            System.out.println("Pago: " + v.getPago().getMetodoPago());
        });

        ventaRepository.buscarConDetalleYProductoPorId(venta.getId()).ifPresent(v -> {
            v.getDetalles().forEach(d -> System.out.println(" - " + d.getProducto().getNombre()));
        });

        System.out.println("\n================ FIN DEMO ================\n");
    }
}