package com.cibertec.tiendavirtual.repository;

import com.cibertec.tiendavirtual.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Integrante 2 - Cliente
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);


    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.pedidos p")
    List<Cliente> buscarClientesConPedidos();


    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.ventas WHERE c.id = :id")
    Optional<Cliente> buscarConVentas(@Param("id") Long id);


    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombres) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Cliente> buscarPorNombreOApellido(@Param("texto") String texto);
}
