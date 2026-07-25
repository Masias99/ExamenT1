package com.cibertec.tiendavirtual.repository;

import com.cibertec.tiendavirtual.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Integrante 1 - Producto
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {


    List<Producto> findByNombreContainingIgnoreCase(String nombre);


    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId")
    List<Producto> buscarPorCategoria(@Param("categoriaId") Long categoriaId);


    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max ORDER BY p.precio ASC")
    List<Producto> buscarPorRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);


    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.stock > :stockMinimo")
    List<Producto> buscarConStockDisponibleConCategoria(@Param("stockMinimo") Integer stockMinimo);
}
