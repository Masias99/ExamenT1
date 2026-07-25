package com.cibertec.tiendavirtual.controller;

import com.cibertec.tiendavirtual.dto.ProductoDTO;
import com.cibertec.tiendavirtual.dto.ProductoRequestDTO;
import com.cibertec.tiendavirtual.exception.ResourceNotFoundException;
import com.cibertec.tiendavirtual.model.*;
import com.cibertec.tiendavirtual.repository.CategoriaRepository;
import com.cibertec.tiendavirtual.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(
                productoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
        return ResponseEntity.ok(toDTO(producto));
    }

    // Derived query: busqueda por nombre (contiene, ignora mayus/minus)
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(
                productoRepository.findByNombreContainingIgnoreCase(nombre)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }


    @GetMapping("/por-categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(
                productoRepository.buscarPorCategoria(categoriaId)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }


    @GetMapping("/por-rango-precio")
    public ResponseEntity<List<ProductoDTO>> buscarPorRangoPrecio(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(
                productoRepository.buscarPorRangoPrecio(min, max)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }


    @GetMapping("/con-stock")
    public ResponseEntity<List<ProductoDTO>> buscarConStockDisponible(
            @RequestParam(defaultValue = "0") Integer stockMinimo) {
        return ResponseEntity.ok(
                productoRepository.buscarConStockDisponibleConCategoria(stockMinimo)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoRequestDTO dto) {
        Producto producto = construirDesdeDTO(dto);
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody ProductoRequestDTO dto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id " + dto.getCategoriaId()));

        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());
        existente.setCategoria(categoria);

        if (existente instanceof Ropa && dto.getTalla() != null) {
            ((Ropa) existente).setTalla(dto.getTalla());
            ((Ropa) existente).setColor(dto.getColor());
        } else if (existente instanceof Calzado) {
            ((Calzado) existente).setTalla(dto.getTalla());
            ((Calzado) existente).setMaterial(dto.getMaterial());
        } else if (existente instanceof Accesorio) {
            ((Accesorio) existente).setTipoAccesorio(dto.getTipoAccesorio());
        }

        return ResponseEntity.ok(toDTO(productoRepository.save(existente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id " + id);
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    private Producto construirDesdeDTO(ProductoRequestDTO dto) {
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("El campo 'tipo' es obligatorio (ROPA, CALZADO o ACCESORIO)");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id " + dto.getCategoriaId()));

        switch (dto.getTipo().toUpperCase()) {
            case "ROPA":
                return Ropa.builder()
                        .nombre(dto.getNombre())
                        .precio(dto.getPrecio())
                        .stock(dto.getStock())
                        .categoria(categoria)
                        .talla(dto.getTalla())
                        .color(dto.getColor())
                        .build();
            case "CALZADO":
                return Calzado.builder()
                        .nombre(dto.getNombre())
                        .precio(dto.getPrecio())
                        .stock(dto.getStock())
                        .categoria(categoria)
                        .talla(dto.getTalla())
                        .material(dto.getMaterial())
                        .build();
            case "ACCESORIO":
                return Accesorio.builder()
                        .nombre(dto.getNombre())
                        .precio(dto.getPrecio())
                        .stock(dto.getStock())
                        .categoria(categoria)
                        .tipoAccesorio(dto.getTipoAccesorio())
                        .build();
            default:
                throw new IllegalArgumentException("Tipo de producto invalido: " + dto.getTipo()
                        + " (valores validos: ROPA, CALZADO, ACCESORIO)");
        }
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO.ProductoDTOBuilder builder = ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .categoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null);

        if (p instanceof Ropa) {
            Ropa r = (Ropa) p;
            builder.tipo("ROPA").talla(r.getTalla()).color(r.getColor());
        } else if (p instanceof Calzado) {
            Calzado c = (Calzado) p;
            builder.tipo("CALZADO").talla(c.getTalla()).material(c.getMaterial());
        } else if (p instanceof Accesorio) {
            Accesorio a = (Accesorio) p;
            builder.tipo("ACCESORIO").tipoAccesorio(a.getTipoAccesorio());
        }

        return builder.build();
    }
}
