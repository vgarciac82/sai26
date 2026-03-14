package com.axtel.sisecop.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.axtel.sisecop.dto.ProductDTO;
import com.axtel.sisecop.entities.ProyectoProducto;
import com.axtel.sisecop.repostories.ProductRepository;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class ProductService extends DataSourceManager {

    private ProductRepository productRepository;

    public ProductService() {
        super();
        this.productRepository = new ProductRepository();
    }

    public ProyectoProducto create(ProductDTO product) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            ProyectoProducto projectProduct = productRepository.insertProduct(connection, product);
            connection.commit();
            return projectProduct;
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public void deleteServicioProducto(int servicioProductoId) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            productRepository.deleteProduct(connection, servicioProductoId);
            connection.commit();
        } catch (SQLException e) {
            Util.rollback(connection);
            throw e;
        } finally {
            CloseObject.closeObject(connection);
        }
    }

    public List<ProyectoProducto> readByProjectID(int projectID) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return productRepository.readByServicioId(connection, projectID);
        } finally {
            CloseObject.closeObject(connection);
        }
    }
}
