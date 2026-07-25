package com.cibertec.tiendavirtual.repository;

import com.cibertec.tiendavirtual.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByNombre(String nombre);
}
