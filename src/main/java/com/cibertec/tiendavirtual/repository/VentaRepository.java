package com.cibertec.tiendavirtual.repository;

import com.cibertec.tiendavirtual.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteId(Long clienteId);


    @Query("SELECT v.cliente.id, SUM(v.total) FROM Venta v GROUP BY v.cliente.id")
    List<Object[]> totalVendidoPorCliente();


    @Query("SELECT v FROM Venta v " +
            "LEFT JOIN FETCH v.pago " +
            "WHERE v.id = :ventaId")
    Optional<Venta> buscarConPagoPorId(@Param("ventaId") Long ventaId);


    @Query("SELECT DISTINCT v FROM Venta v " +
            "LEFT JOIN FETCH v.detalles d " +
            "LEFT JOIN FETCH d.producto " +
            "WHERE v.id = :ventaId")
    Optional<Venta> buscarConDetalleYProductoPorId(@Param("ventaId") Long ventaId);


    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.total >= :montoMinimo " +
            "ORDER BY v.fechaVenta DESC")
    List<Venta> buscarPorFechaYMontoMinimo(@Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin,
                                           @Param("montoMinimo") BigDecimal montoMinimo);
}