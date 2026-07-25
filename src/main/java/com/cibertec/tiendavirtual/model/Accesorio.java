package com.cibertec.tiendavirtual.model;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "accesorio")
@DiscriminatorValue("ACCESORIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Accesorio extends Producto {

    @Column(name = "tipo_accesorio", length = 50)
    private String tipoAccesorio;
}
