
-- PROYECTO: Tienda Virtual de Ropa
-- Script de Base de Datos para MySQL Workbench


DROP DATABASE IF EXISTS tienda_virtual_web;
CREATE DATABASE tienda_virtual_web CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_virtual_web;


-- 1. CATEGORIA

CREATE TABLE categoria (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100)  NOT NULL,
    descripcion VARCHAR(255)
);


CREATE TABLE producto (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150)   NOT NULL,
    precio       DECIMAL(10,2)  NOT NULL,
    stock        INT            NOT NULL DEFAULT 0,
    tipo         VARCHAR(31)    NOT NULL,          -- discriminator (informativo)
    categoria_id BIGINT         NOT NULL,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);


CREATE TABLE ropa (
    id     BIGINT PRIMARY KEY,
    talla  VARCHAR(10),
    color  VARCHAR(50),
    CONSTRAINT fk_ropa_producto
        FOREIGN KEY (id) REFERENCES producto(id)
);


CREATE TABLE calzado (
    id        BIGINT PRIMARY KEY,
    talla     VARCHAR(10),
    material  VARCHAR(50),
    CONSTRAINT fk_calzado_producto
        FOREIGN KEY (id) REFERENCES producto(id)
);


CREATE TABLE accesorio (
    id               BIGINT PRIMARY KEY,
    tipo_accesorio   VARCHAR(50),
    CONSTRAINT fk_accesorio_producto
        FOREIGN KEY (id) REFERENCES producto(id)
);


CREATE TABLE cliente (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres    VARCHAR(100) NOT NULL,
    apellidos  VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    telefono   VARCHAR(20),
    direccion  VARCHAR(200)
);


CREATE TABLE pedido (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_pedido  DATETIME       NOT NULL,
    estado        VARCHAR(30)    NOT NULL,   -- PENDIENTE, CONFIRMADO, CANCELADO
    cliente_id    BIGINT         NOT NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);


CREATE TABLE detalle_pedido (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id        BIGINT         NOT NULL,
    producto_id      BIGINT         NOT NULL,
    cantidad         INT            NOT NULL,
    precio_unitario  DECIMAL(10,2)  NOT NULL,
    CONSTRAINT fk_detpedido_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_detpedido_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
);


CREATE TABLE venta (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta  DATETIME       NOT NULL,
    total        DECIMAL(10,2)  NOT NULL,
    cliente_id   BIGINT         NOT NULL,
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);


CREATE TABLE detalle_venta (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id         BIGINT         NOT NULL,
    producto_id      BIGINT         NOT NULL,
    cantidad         INT            NOT NULL,
    precio_unitario  DECIMAL(10,2)  NOT NULL,
    CONSTRAINT fk_detventa_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id),
    CONSTRAINT fk_detventa_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
);


CREATE TABLE pago (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id      BIGINT         NOT NULL UNIQUE,
    metodo_pago   VARCHAR(30)    NOT NULL,  -- TARJETA, YAPE, PLIN, EFECTIVO
    monto         DECIMAL(10,2)  NOT NULL,
    fecha_pago    DATETIME       NOT NULL,
    CONSTRAINT fk_pago_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id)
);


-- DATOS DE PRUEBA

INSERT INTO categoria (nombre, descripcion) VALUES
 ('Ropa de Hombre', 'Prendas para caballero'),
 ('Calzado Deportivo', 'Zapatillas y calzado deportivo'),
 ('Accesorios', 'Complementos de moda');

INSERT INTO cliente (nombres, apellidos, email, telefono, direccion) VALUES
 ('Ana', 'Torres Ramos', 'ana.torres@mail.com', '987654321', 'Av. Siempre Viva 123'),
 ('Luis', 'Gómez Pérez', 'luis.gomez@mail.com', '912345678', 'Jr. Los Olivos 456');


INSERT INTO producto (nombre, precio, stock, tipo, categoria_id) VALUES
 ('Polo Algodón Premium', 49.90, 100, 'ROPA', 1),
 ('Zapatilla Running X1', 199.90, 50, 'CALZADO', 2),
 ('Gorra Urbana', 39.90, 80, 'ACCESORIO', 3);


INSERT INTO ropa (id, talla, color) VALUES (1, 'M', 'Azul');


INSERT INTO calzado (id, talla, material) VALUES (2, '42', 'Malla transpirable');


INSERT INTO accesorio (id, tipo_accesorio) VALUES (3, 'Gorra');
