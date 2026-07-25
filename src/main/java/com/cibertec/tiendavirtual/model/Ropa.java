package com.cibertec.tiendavirtual.model;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ropa")
@DiscriminatorValue("ROPA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Ropa extends Producto {

    @Column(length = 10)
    private String talla;

    @Column(length = 50)
    private String color;
}
