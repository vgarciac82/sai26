package com.axtel.sisecop.dto;

import java.util.Base64;

public class ProductDTO {

    private String descripcion;

    private int productoId;

    private int servicioId;

    private int servicioproductoId;

    public String getDescripcion() {
        return descripcion;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getServicioId() {
        return servicioId;
    }

    public int getServicioproductoId() {
        return servicioproductoId;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public void setServicioproductoId(int servicioproductoId) {
        this.servicioproductoId = servicioproductoId;
    }

    @Override
    public String toString() {
        return "ProductDTO [servicioId=" + servicioId + ", productoId=" + productoId + ", descripcion=" + descripcion + ", servicioproductoId=" + servicioproductoId + "]";
    }
}
