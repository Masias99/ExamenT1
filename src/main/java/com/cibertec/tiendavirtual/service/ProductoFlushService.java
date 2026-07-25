package com.cibertec.tiendavirtual.service;

import com.cibertec.tiendavirtual.model.Categoria;
import com.cibertec.tiendavirtual.model.Ropa;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class ProductoFlushService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void registrarLoteDeRopa(Categoria categoria, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Ropa ropa = Ropa.builder()
                    .nombre("Polo Basico " + (i + 1))
                    .precio(BigDecimal.valueOf(29.90))
                    .stock(20)
                    .categoria(categoria)
                    .talla("M")
                    .color("Negro")
                    .build();

            entityManager.persist(ropa);


            if (i % 20 == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
    }
}
