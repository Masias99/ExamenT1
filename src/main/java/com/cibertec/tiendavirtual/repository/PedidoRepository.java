package com.cibertec.tiendavirtual.repository;

import com.cibertec.tiendavirtual.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(String estado);


    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId ORDER BY p.fechaPedido DESC")
    List<Pedido> buscarPorCliente(@Param("clienteId") Long clienteId);


    @Query("SELECT DISTINCT p FROM Pedido p " +
           "LEFT JOIN FETCH p.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE p.id = :pedidoId")
    Optional<Pedido> buscarConDetalleYProducto(@Param("pedidoId") Long pedidoId);


    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin")
    List<Pedido> buscarPorRangoFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
