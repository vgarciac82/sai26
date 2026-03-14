package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoProducto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProyectoClaseProducto productoClase;

    private String productoDescripcion;

    private int productoId;

    private String productoNombre;

    private int servicioProductoID;

    public ProyectoClaseProducto getProductoClase() {
        return productoClase;
    }

    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public int getServicioProductoID() {
        return servicioProductoID;
    }

    public void setProductoClase(ProyectoClaseProducto productoClase) {
        this.productoClase = productoClase;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public void setServicioProductoID(int servicioProductoID) {
        this.servicioProductoID = servicioProductoID;
    }

    @Override
    public String toString() {
        return "ProyectoProducto [productoId=" + productoId + ", productoNombre=" + productoNombre + ", productoDescripcion=" + productoDescripcion + ", productoClase=" + productoClase + "]";
    }
}
